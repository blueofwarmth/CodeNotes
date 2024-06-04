package com.rpc.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qyingli
 * @date 2024/5/17 14:59
 * @package: com.rpc.spi
 * @description: TODO SPI机制
 */
public class ExtensionLoader {
    private Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    // 系统SPI
    private static String SYS_EXTENSION_LOADER_DIR_PREFIX = "META-INF/xrpc/";

    // 用户SPI
    private static String DIY_EXTENSION_LOADER_DIR_PREFIX = "META-INF/rpc/";

    private static String[] prefixs = {SYS_EXTENSION_LOADER_DIR_PREFIX, DIY_EXTENSION_LOADER_DIR_PREFIX};

    //两种加载模式
    // bean定义信息 key: 定义的key value：具体类, 根据key获取类
    private static Map<String, Class> extensionClassCache = new ConcurrentHashMap<>();
    // bean 定义信息 key：接口 value：接口子类, 根据接口获取接口子类
    private static Map<String, Map<String,Class>> extensionClassCaches = new ConcurrentHashMap<>();

    // 实例化的bean, 即按需加载, 和Java的不同
    private static Map<String, Object> singletonsObject = new ConcurrentHashMap<>();


    private static ExtensionLoader extensionLoader;

    static {
        extensionLoader = new ExtensionLoader();
    }

    public static ExtensionLoader getInstance(){
        return extensionLoader;
    }

    private ExtensionLoader(){}
/*****************************************/
    /**
     * 根据key获取bean
     * @param key
     * @return
     * @param <V>
     */
    public <V> V getBeanByKey(String key) {
        //不存在
        try {
            if (!singletonsObject.containsKey(key)) {
                singletonsObject.put(key, extensionClassCache.get(key).newInstance());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (V) singletonsObject.get(key);
    }

    /**
     * 根据接口获取接口子类
     * @param clazz
     * @return
     */
    public List<Object> getBeanByClass(Class clazz) {
        String name = clazz.getName();
        if(!extensionClassCaches.containsKey(name)) {
            try {
                throw new ClassNotFoundException(clazz + "未找到");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        final Map<String, Class> stringClassMap = extensionClassCaches.get(clazz);
        List<Object> objects = new ArrayList<>();
        if(stringClassMap.size() > 0) {
            stringClassMap.forEach((key, value) -> {
                try {
                    objects.add(singletonsObject.getOrDefault(key, value.newInstance()));
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return objects;
    }

    /**
     * 根据spi机制初加载bean的信息放入map
     * @param clazz
     * @throws ClassNotFoundException
     */
    public void loadExtension(Class clazz) throws ClassNotFoundException, IOException {
        if(clazz == null) {
            throw new NullPointerException("class不能为空");
        }
        //获取当前类的类加载器
        ClassLoader classLoader = this.getClass().getClassLoader();
        //从用户spi和系统spi中获取bean放入map
        HashMap<String, Class> stringClassHashMap = new HashMap<>();
        for (String prefix : prefixs) {
            String spiFilePath = prefix + clazz.getName();
            Enumeration<URL> enumeration = classLoader.getResources(spiFilePath);
            //读取配置文件
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                InputStreamReader inputStreamReader = null;
                inputStreamReader = new InputStreamReader(url.openStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                //获取到配置文件信息
                while ((line = bufferedReader.readLine()) != null) {
                    String[] lineArr = line.split("=");
                    String key = lineArr[0];
                    String name = lineArr[1];
                    final Class<?> aClass = Class.forName(name);
                    extensionClassCache.put(key, aClass);
                    stringClassHashMap.put(key, aClass);
                    logger.info("加载bean key:{} , value:{}",key,name);
                }
            }
        }
        extensionClassCaches.put(clazz.getName(),stringClassHashMap);
    }

}
