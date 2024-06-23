# annotation

## EableXXXX

开启自动装配, 与springboot进行整合, 一般为后置处理器

## RPCReference

调用方注解, 各种属性

如版本, 超时时间, 负载均衡策略, 容错机制, 重试次数

## RPCService

指定RPC服务和版本

# Common

服务方和提供方公用的一些模块

## constants

常量包, 进行统一管理

### 默认容错机制

* 故障转移
* 快速失败
* 快速保护

### 负载均衡规则

轮询算法（Round Robin Algorithm）和一致性哈希算法（Consistent Hashing Algorithm）是两种常见的负载均衡和分布式系统中的数据分配策略。它们用于将请求分发到多个服务器或节点上，以实现负载均衡和数据存储的一致性。

### 轮询算法（Round Robin Algorithm）

#### 基本概念

轮询算法是一种简单且常用的负载均衡算法。它依次将请求分发到一组服务器中，从第一台服务器开始，依次循环，周而复始。这种方法在所有服务器性能均等的情况下，能够较好地实现负载均衡。

#### 特点

- **简单易实现**：轮询算法的实现非常简单，不需要复杂的计算和存储。
- **负载均衡**：能够将请求平均分配到所有服务器上。
- **无状态**：不需要维护额外的状态信息，适合于无状态的负载均衡器。

#### 示例

假设有三台服务器 A、B、C，按照轮询算法的请求分发顺序如下：

1. 第一个请求分配给服务器 A
2. 第二个请求分配给服务器 B
3. 第三个请求分配给服务器 C
4. 第四个请求再次分配给服务器 A
5. 如此循环往复

```java
public class RoundRobin {
    private int index = 0;
    private List<String> servers;

    public RoundRobin(List<String> servers) {
        this.servers = servers;
    }

    public synchronized String getNextServer() {
        if (servers.isEmpty()) {
            return null;
        }
        String server = servers.get(index);
        index = (index + 1) % servers.size();
        return server;
    }

    public static void main(String[] args) {
        List<String> servers = Arrays.asList("A", "B", "C");
        RoundRobin roundRobin = new RoundRobin(servers);
        for (int i = 0; i < 10; i++) {
            System.out.println(roundRobin.getNextServer());
        }
    }
}
```

### 一致性哈希算法（Consistent Hashing Algorithm）

#### 基本概念

一致性哈希算法是一种用于分布式系统的数据分布策略，能够在节点增减时尽量减少数据的迁移量。它通过将所有节点和数据映射到一个逻辑环上来实现。每个节点在环上有一个位置，数据根据哈希值映射到环上的位置，顺时针找到的第一个节点就是该数据的存储节点。

会拓展出虚拟节点, 解决数据倾斜, 即在哈希环上节点之间距离过大或者过小

#### 特点

- **高效容错**：在节点增加或减少时，只有部分数据需要重新分配，减少了数据迁移量。
- **负载均衡**：通过虚拟节点技术，可以提高负载均衡的效果。
- **适用于分布式系统**：特别适合于动态变化的分布式环境。

#### 示例

假设有三个服务器节点，使用一致性哈希算法的逻辑如下：

1. 将每个服务器节点通过哈希函数映射到环上的位置。
2. 将数据通过哈希函数映射到环上的位置。
3. 数据存储在顺时针方向上遇到的第一个服务器节点上。

```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {
    private static class Node {
        String name;

        Node(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final SortedMap<Integer, Node> ring = new TreeMap<>();
    private final int numberOfReplicas;

    public ConsistentHashing(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public void add(Node node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            ring.put(hash(node.toString() + i), node);
        }
    }

    public void remove(Node node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            ring.remove(hash(node.toString() + i));
        }
    }

    public Node get(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        int hash = hash(key);
        if (!ring.containsKey(hash)) {
            SortedMap<Integer, Node> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes());
            return ((digest[3] & 0xFF) << 24) | ((digest[2] & 0xFF) << 16) | ((digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ConsistentHashing consistentHashing = new ConsistentHashing(3);
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");

        consistentHashing.add(nodeA);
        consistentHashing.add(nodeB);
        consistentHashing.add(nodeC);

        String[] keys = {"key1", "key2", "key3", "key4"};
        for (String key : keys) {
            System.out.println("Key " + key + " is mapped to " + consistentHashing.get(key));
        }
    }
}
```

### 区别总结

- **复杂性**：
  - **轮询算法**：实现简单，适用于节点数较少且变动不频繁的场景。
  - **一致性哈希算法**：实现相对复杂，但适用于节点频繁增减的分布式系统。

- **负载均衡**：
  - **轮询算法**：假设所有节点性能一致，可以实现较好的负载均衡。
  - **一致性哈希算法**：通过虚拟节点技术，可以实现更加均衡的负载分布。

- **数据迁移**：
  - **轮询算法**：不涉及数据迁移。
  - **一致性哈希算法**：节点增减时，仅部分数据需要迁移。

- **适用场景**：
  - **轮询算法**：适用于简单的负载均衡需求，例如 Web 服务器的请求分发。
  - **一致性哈希算法**：适用于分布式缓存、分布式数据库等需要高可用和动态扩展的场景。

通过对比，可以看到这两种算法各有优势，具体选择哪种算法应根据实际需求和系统特性来决定。

* 一致性哈希
* 轮询

### 消息当前状态

### 消息类型

* 请求
* 响应

### 注册规则

使用redis作为注册中心

### 序列化规则

* json格式
* hession

## RpcFuture

netty中future代表了一个异步操作的结果。它提供了一种机制，用于处理和管理 I/O 操作，

特别是在网络编程中。

即用于获取操作结果, 存放在Promise当前

## PRCRequest&Respond

请求/响应体, 各种所需的属性

## RpcServiceNameBuilder

构建key

名字+版本 => key

## ServiceMeta

服务元数据

地址, 端口, id等等

重写了equals, 为后面容错机制使用

# config

从配置文件获取信息

# provider

## ProviderPostProcessor

### 读取配置文件信息

使用EnvironmentAware, 利用工具类读取内容

### 启动服务

* 配置netty, 启动服务器
* 初始化相关组件

### 服务注册

#### 反射获取服务信息

Java 中的 `Class` 类是反射机制的核心，它代表一个正在运行的 Java 应用程序中的类或接口。通过 `Class` 对象，你可以获取关于类的信息，包括类的名称、方法、构造函数、字段、注解等，同时还可以动态地创建类实例、调用方法和访问字段。

* 获取 `Class` 对象

有多种方法可以获得一个类的 `Class` 对象：

1. **通过类的静态成员 `.class`**：
   
   ```java
   Class<String> stringClass = String.class;
   ```
   
2. **通过 `Object` 的 `getClass()` 方法**：
   ```java
   String str = "Hello";
   Class<?> stringClass = str.getClass();
   ```

3. **通过 `Class.forName()` 方法**：
   ```java
   Class<?> stringClass = Class.forName("java.lang.String");
   ```

* 使用class

使用 `newInstance()` 方法（已过时，推荐使用 `Constructor` 对象）：
```java
Class<?> clazz = String.class;
String str = (String) clazz.getDeclaredConstructor().newInstance();
```

以下是一个完整的示例，展示如何通过 `Class` 对象获取类信息并调用其方法：

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionExample {
    public static void main(String[] args) {
        try {
            // 获取 String 类的 Class 对象
            Class<?> clazz = Class.forName("java.lang.String");

            // 获取类名
            System.out.println("Class Name: " + clazz.getName());

            // 获取构造函数
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            System.out.println("Constructors:");
            for (Constructor<?> constructor : constructors) {
                System.out.println(constructor);
            }

            // 获取方法
            Method[] methods = clazz.getDeclaredMethods();
            System.out.println("Methods:");
            for (Method method : methods) {
                System.out.println(method.getName());
            }

            // 调用方法
            Method substringMethod = clazz.getDeclaredMethod("substring", int.class);
            String result = (String) substringMethod.invoke("Hello, world!", 7);
            System.out.println("Result of substring: " + result);

            // 获取字段
            Field[] fields = clazz.getDeclaredFields();
            System.out.println("Fields:");
            for (Field field : fields) {
                System.out.println(field.getName());
            }

            // 访问私有字段
            Field privateField = clazz.getDeclaredField("value");
            privateField.setAccessible(true);
            char[] value = (char[]) privateField.get("Hello");
            System.out.println("Value of private field: " + new String(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### 通过注册中心注册

* 从配置文件中获得注册中心类型

* 设置属性后注册
* 放入到缓存中

# registry

## RedisService

### 服务注册

拿到使用中的serviceMeta

### 服务注销

### 服务关闭

### 获取所有服务



## ->RedisRegistry

思路：

使用集合保存所有服务节点信息


* 1. 服务启动：节点使用了redis作为注册中心后，将自身信息注册到redis当中(ttl：10秒)，并开启定时任务，ttl/2。
* 2. 定时任务用于检测各个节点的信息，如果发现节点的时间 < 当前时间->则将节点踢出，如果没有发现->则续签自身节点
* 3. 将节点踢出后，从服务注册表中找到对应key删除该节点的下的服务数据信息
* ttl :10秒
* 定时任务为ttl/2
* 节点注册后启动心跳检测，检测服务注册的key集合，如果有服务到期，则删除,自身的服务则续签
* 服务注册后将服务注册到redis以及保存到自身的服务注册key集合，供心跳检测
* 如果有节点宕机，则其他服务会检测的，如果服务都宕机，则ttl会进行管理
* 使用集合存储服务节点信息

#### 注册

* 构建一个当前服务的key
* 添加到ServiceMap中
* 创建Jedis
* 将ServiceMeta转化为Json格式
* 加入到Jedis

### 注册

1. 为当前服务构建key, 设置UUID和结束时间
2. 转化为JSON字符串, 注册到Jedis

Jedis获取会从JedisPool中拿到

###  注册中心构造器

1. 拿到配置文件中拿到服务的信息
2. 设置JedisPool容量
3. 注册到JedisPool中
4. 运行定时任务

### 定时任务

每个服务在注册中心都有自己的ttl, 注册中心会通过ScheduledExecutorService轮询, 检测服务的超时时间. 

1. 通过key获得服务节点, 遍历检查所有节点的endTime

2. 超时了 -> 从注册中心删除
3. 如果当前服务是自身节点, 即UUID相等, 则续签
4. 重新加载服务

## Jedis

Jedis 是一个流行的 Java 客户端库，用于与 Redis 数据库进行交互。Redis 是一个基于内存的数据存储系统，提供了各种数据结构的支持，并且支持持久化，发布/订阅，事务等功能。Jedis 提供了一系列简单易用的 API，使 Java 开发者能够方便地与 Redis 数据库进行通信和操作。

**主要特点**

1. **简单易用**：Jedis 提供了直观且易于理解的 API，使开发者能够快速上手，并且可以轻松地执行 Redis 命令。
2. **高性能**：Jedis 是一个高性能的客户端库，与 Redis 服务器之间的通信采用了 Socket 连接，通过网络传输数据，实现了快速的数据交互。
3. **完整支持**：Jedis 完整支持 Redis 的所有功能，包括各种数据类型的操作、事务、发布/订阅、管道（pipeline）、集群等。

**示例代码**

以下是一个简单的 Jedis 使用示例，演示了如何连接 Redis 服务器并执行基本的操作：

```java
import redis.clients.jedis.Jedis;

public class JedisExample {
    public static void main(String[] args) {
        // 连接到本地 Redis 服务
        Jedis jedis = new Jedis("localhost", 6379);

        // 执行 Redis 命令
        jedis.set("key", "value");
        String value = jedis.get("key");
        System.out.println("Value: " + value);

        // 关闭连接
        jedis.close();
    }
}
```

在上面的示例中，我们首先创建了一个 Jedis 对象，指定了 Redis 服务器的地址和端口。然后，我们通过 `set` 方法设置了一个键值对，再通过 `get` 方法获取了对应的值。

**注意事项**

- 在使用 Jedis 连接 Redis 服务器时，需要确保 Redis 服务器已经启动，并且能够通过指定的地址和端口访问到。
- 在生产环境中，为了避免 Jedis 连接泄漏和资源浪费，需要正确地管理 Jedis 连接的生命周期，及时关闭连接。
- 在 Jedis 的连接池中管理连接可以提高性能和资源利用率，因此在高并发场景下推荐使用连接池来管理连接。
- 注意 Jedis API 的线程安全性，如果在多线程环境下使用 Jedis，需要进行适当的同步处理，或者考虑使用线程安全的 Redisson 等替代方案。

### 常用方法

Jedis 提供了丰富的方法来执行各种 Redis 操作，包括字符串操作、哈希操作、列表操作、集合操作、有序集合操作、事务、发布/订阅等。以下是 Jedis 中一些常用的方法示例：

#### 连接管理

- **连接到 Redis 服务器**：

  ```java
  Jedis jedis = new Jedis("localhost", 6379);
  ```

- **关闭连接**：

  ```java
  jedis.close();
  ```

#### 字符串操作

- **设置键值对**：

  ```java
  jedis.set("key", "value");
  ```

- **获取键的值**：

  ```java
  String value = jedis.get("key");
  ```

### 哈希操作

- **设置哈希字段值**：

  ```java
  jedis.hset("hash", "field", "value");
  ```

- **获取哈希字段值**：

  ```java
  String fieldValue = jedis.hget("hash", "field");
  ```

#### 列表操作

- **向列表中添加元素**：

  ```java
  jedis.lpush("list", "element1", "element2");
  ```

- **获取列表范围内的元素**：

  ```java
  List<String> elements = jedis.lrange("list", 0, -1);
  ```

#### 集合操作

- **向集合中添加元素**：

  ```java
  jedis.sadd("set", "member1", "member2");
  ```

- **获取集合中的所有成员**：

  ```java
  Set<String> members = jedis.smembers("set");
  ```

#### 有序集合操作

- **向有序集合中添加成员和分数**：

  ```java
  jedis.zadd("sortedset", 1.0, "member1", 2.0, "member2");
  ```

- **获取有序集合范围内的成员**：

  ```java
  Set<String> members = jedis.zrange("sortedset", 0, -1);
  ```

### 事务

- **开启事务**：

  ```java
  Transaction transaction = jedis.multi();
  ```

- **执行事务中的命令**：

  ```java
  transaction.set("key", "value");
  transaction.hset("hash", "field", "value");
  ```

- **提交事务**：

```Java
transaction.exec();
```

## ->Factory

SPI机制, 注册到spring中

# SPI

### API（Application Programming Interface）

#### 基本概念

API 是应用程序提供给其他程序使用的一组定义。它定义了各种类、接口、方法等的规范和用法，作为不同软件组件之间的通信桥梁。API 定义了程序员可以调用的功能，并指定了调用这些功能的方式。

实现和接口在一个包中。自定义接口，自实现类。

#### 特点

- **规范性**：API 规定了软件组件之间的接口和交互方式，确保了不同组件之间的协同工作。
- **稳定性**：API 一旦确定，一般不会轻易修改，以保证向后兼容性。
- **抽象性**：API 隐藏了具体实现的细节，提供了高层次的抽象接口，方便程序员使用。

### SPI（Service Provider Interface）

#### 基本概念

SPI 是一种软件设计模式，用于在运行时动态加载和注册服务提供者。在 SPI 中，服务的提供者（Provider）实现了特定的接口或抽象类，然后通过 SPI 机制将其注册到系统中。SPI 提供了一种可插拔式的架构，允许系统在运行时动态选择并加载合适的服务提供者。

「接口」在「调用方」的包，「调用方」定义规则，而自定义实现类在「实现方」的包，然后把实现类加载到「调用方」中。

#### 特点

- **动态性**：SPI 允许系统在运行时动态加载和注册服务提供者，实现了可插拔式的架构。
- **扩展性**：SPI 提供了一种灵活的扩展机制，可以轻松地添加、删除或替换服务提供者。
- **松耦合**：SPI 降低了系统组件之间的耦合度，提高了系统的灵活性和可维护性。

#### 示例

Java 的 SPI 机制允许开发者定义服务接口，并为其定义一组服务实现提供者。服务实现者通过在 `META-INF/services` 目录下创建以接口全限定名为文件名的文件，并在文件中列出其提供的服务实现类，从而实现服务注册。

```java
// 服务调用者定义服务接口
public interface HelloService {
    void sayHello();
}

// 服务提供者实现接口
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("Hello from HelloServiceImpl");
    }
}

// 在 META-INF/services 目录下创建文件 com.example.HelloService，并写入服务提供者的类名
// 文件内容：com.example.HelloServiceImpl

// 使用 SPI 加载服务
Iterator<HelloService> providers = ServiceLoader.load(HelloService.class).iterator();
while (providers.hasNext()) {
    HelloService service = providers.next();
    service.sayHello(); // 输出：Hello from HelloServiceImpl
}
```

### 区别总结

- **角色定位**：
  - **API**：定义了应用程序的接口和规范，提供了程序员调用的功能。
  - **SPI**：定义了服务提供者的接口规范，允许系统在运行时动态加载和注册服务。

- **使用场景**：
  - **API**：用于定义应用程序的外部接口和交互规范。
  - **SPI**：用于实现可插拔式的架构，动态加载和注册服务提供者。

- **关注点**：
  - **API**：关注接口的定义和使用。
  - **SPI**：关注服务提供者的注册和加载。

通过 API 和 SPI 的结合使用，可以实现复杂系统的模块化和可扩展性，提高系统的灵活性和可维护性。

## 实现

* 定义spi路径

```Java
    // 系统SPI
    private static String SYS_EXTENSION_LOADER_DIR_PREFIX = "META-INF/xrpc/";

    // 用户SPI
    private static String DIY_EXTENSION_LOADER_DIR_PREFIX = "META-INF/rpc/";

    private static String[] prefixs = {SYS_EXTENSION_LOADER_DIR_PREFIX, DIY_EXTENSION_LOADER_DIR_PREFIX};
```

* 定义加载机制

```
//两种加载模式
//根据key获取类
private static Map<String, Class> extensionClassCache = new ConcurrentHashMap<>();
//根据接口获取接口子类
private static Map<String, Map<String,Class>> extensionClassCaches = new ConcurrentHashMap<>();

// 实例化的bean, 即按需加载, 和Java的不同
private static Map<String, Object> singletonsObject = new ConcurrentHashMap<>();
```

### 根据key获取bean

如果不存在, 则在extensionClassCache的Map中创建一个key的对象

`extensionClassCache.get(key).newInstance())`

### 根据接口获取bean

1. 先用反射获取类名, 判断是否在extensionClassCaches map中存在
2. 在extensionClassCaches中获取对应的map
3. 遍历map, 将子类bean添加到List集合中

### 加载bean并放入map

1. 用类加载器从spi的路径中获取到对应的类

`ClassLoader classLoader = this.getClass().getClassLoader();`

`classLoader.getResources(spiFilePath);`

2. 从配置文件中获取信息



# protocol

## 解码&编码

### 编码

1. 获取消息各项属性
2. 指定序列化器
3. 将序列化结果写入到流

### 解码

1. 判断流中的序列化数组结果长度是否符合规范
2. 标记当前位置
3. 从流中获取消息的各项属性
4. 判断消息体长度是否正常
5. 构建消息
6. 反序列化
7. 判断消息类型(请求/响应), 做对应的处理

## handler

### consumer

消费方响应

### service

处理消费方的数据

前置和后置拦截器

### serialization

* 工厂模式

1. 定义序列化接口
2. 实现序列化
3. 定义序列化工厂, 有SPI提供具体对象

序列化/反序列化

#### JSON序列化

### 序列化异常

### RpcRequestProcessor

业务线程池

## 协议&消息头

协议 -> 消息头 + 消息体



# Consumer

## ConsumerPostProcessor

消费方后置处理器, 服务发现

1. 通过初始化后Bean获取他的所有字段, 也就是调用方对象
2. 判断字段中是否有RpcReference注解
3. 如果该字段标记了RpcReference注解, 则获取该字段的类型
4. 创建一个Object, 为其引用代理对象

```Java
object = Proxy.newProxyInstance(
        refClass.getClassLoader(),
        new Class<?>[]{refClass},
        new RpcInvokerProxy(
                rpcReference.serviceVersion(),
                rpcReference.timeout(),
                rpcReference.faultTolerant(),
                rpcReference.loadBalancer(),
                rpcReference.retryCount()));
```

5. 为该字段设置代理对象

`field.set(bean, object);`

### 获取配置信息

### SPI注入

### 初始化后的操作

## RpcConsumer

消费方发送数据

### netty配置

`EventLoopGroup` 是 Netty 中的一个核心组件，用于管理和调度一组 `EventLoop`。它负责处理所有的 I/O 操作、任务调度以及事件分发。下面详细介绍 `EventLoopGroup` 的作用和用法。

### EventLoopGroup 的作用

1. **线程管理**：
   - `EventLoopGroup` 管理一组线程，每个线程负责一个或多个 `Channel` 的 I/O 操作。它通过内部维护的 `EventLoop` 实例来实现这一点。
   - Netty 提供了多种类型的 `EventLoopGroup`，如 `NioEventLoopGroup`（基于 NIO 的实现）和 `EpollEventLoopGroup`（基于 epoll 的实现，用于 Linux 系统）。

2. **任务调度**：
   - 除了处理 I/O 操作，`EventLoopGroup` 还可以调度普通任务和定时任务。每个 `EventLoop` 都有自己的任务队列，可以执行异步任务。
   - 任务调度的功能类似于 Java 的 `ScheduledExecutorService`，可以执行一次性任务或重复任务。

3. **负载均衡**：
   - `EventLoopGroup` 通过内部的 `EventLoop` 实例实现负载均衡。当有新的 `Channel` 注册时，它会将 `Channel` 分配给一个 `EventLoop`，以均衡每个 `EventLoop` 的负载。
   - 这种机制有助于提升网络应用的性能和可伸缩性。

### 使用示例

以下示例展示了如何在 Netty 中使用 `EventLoopGroup` 来启动一个简单的服务器：

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建 boss 和 worker EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 用于接受客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用于处理 I/O 操作

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     // 配置 ChannelPipeline
                     // p.addLast(new YourHandler());
                 }
             });

            // 绑定端口并启动服务器
            ChannelFuture f = b.bind(8080).sync();
            // 等待服务器 socket 关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅关闭 EventLoopGroup
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```

### 详细说明

1. **Boss Group 和 Worker Group**：
   - `bossGroup` 负责接受客户端的连接。
   - `workerGroup` 负责处理连接的 I/O 操作。
   - 通常，一个 `bossGroup` 会搭配一个或多个 `workerGroup` 使用。

2. **Channel Pipeline**：
   - 每个 `Channel` 都有一个 `ChannelPipeline`，它是一个处理 `Channel` 中各种事件的处理链。
   - 可以向 `ChannelPipeline` 添加多个 `ChannelHandler`，每个 `ChannelHandler` 负责处理特定类型的事件。

3. **绑定端口和启动服务器**：
   - 通过 `ServerBootstrap` 的 `bind` 方法绑定端口并启动服务器。
   - `sync` 方法用于等待绑定操作完成。

4. **优雅关闭**：
   - 在程序退出之前，需要调用 `shutdownGracefully` 方法来关闭 `EventLoopGroup`，以确保所有资源被正确释放。

### 总结

`EventLoopGroup` 在 Netty 中起到了至关重要的作用，它负责管理和调度 I/O 线程，处理所有的网络事件和任务调度。理解和正确使用 `EventLoopGroup` 是高效开发 Netty 应用的基础。通过合理配置 `EventLoopGroup`，可以实现高性能、高可伸缩性的网络应用。

### 发送请求

# Filter

## 日志

## ServiceTokenFilter

token拦截器, 执行业务逻辑之前

重写doFilter

## Filter&Service/ClientAfterFilter&CilentBeforeFilter

工厂模式

filter->after, before

## FilterChain

拦截器链 责任链设计模式

责任链模式是一种行为设计模式，它允许多个对象来处理请求，将这些对象串成一条链，并沿着链传递请求，直到有一个对象能够处理它为止。这个模式解耦了请求发送者和接收者之间的关系，使得多个对象都有机会处理请求，从而增强了系统的灵活性。

### 主要角色

责任链模式主要涉及以下几个角色：

1. **抽象处理者（Handler）**：定义了处理请求的接口，通常包含一个抽象的处理方法或责任链的下一个处理者引用。
   
2. **具体处理者（ConcreteHandler）**：实现了处理请求的方法，并根据自身能力决定是否处理请求，如果自身不能处理，则将请求传递给下一个处理者。

3. **客户端（Client）**：创建责任链，并向其发送请求。

### 实现方式

责任链模式可以通过以下方式实现：

1. **链表结构**：将处理者对象串成一条链表，每个处理者持有下一个处理者的引用，在接收到请求后，判断自身是否能够处理，如果不能则将请求传递给下一个处理者。

2. **递归调用**：每个处理者在处理请求时，都递归调用下一个处理者的处理方法，直到找到能够处理请求的处理者为止。

### 优点

- **松耦合**：责任链模式将请求的发送者和接收者解耦，每个处理者只关心自己能否处理请求，无需知道请求的全貌。
- **灵活性**：可以动态地增加、删除或修改处理者，改变责任链的结构和执行顺序，以满足不同的需求。
- **可扩展性**：新的处理者可以很容易地加入到责任链中，无需修改现有的代码。

### 适用场景

- 当系统需要动态地确定由哪个对象来处理请求时，可以使用责任链模式。
- 当系统中有多个对象可以处理同一请求，但客户端不知道具体处理者时，可以使用责任链模式。
- 当系统需要将请求的处理者组合成一条链，并动态地决定请求的执行顺序时，可以使用责任链模式。

### 示例

一个常见的示例是在 Web 开发中使用的过滤器链。在一个 Web 应用中，经常需要对请求进行一系列的处理，比如权限验证、日志记录、数据校验等。这些处理可以组成一个过滤器链，每个过滤器负责一个特定的处理任务，当一个过滤器不能处理请求时，将请求传递给下一个过滤器，直到找到能够处理请求的过滤器为止。

## FilterConfig

拦截器配置类，用于统一管理拦截器

初始化拦截器

## FilterData

拦截器的上下文

# poll

线程池工厂



# router

负载均衡是基于调用方的

## LoadBalancer

负载均衡,根据负载均衡获取对应的服务节点(负载均衡包装服务节点)

### 选择负载均衡策略



## 负载均衡算法

### 一致性哈希

> 一个服务上会有多个节点

1. 物理节点映射的虚拟节点,为了解决哈希倾斜
2. 获取注册中心
3. 从注册中心获取对应的服务集合(调用方想要调用的那个)
4. 利用TreeMap构建一个哈希环
5. 将拿到的节点分配到构建的哈希环中

### 轮询

1. 获取注册中心
2. 获取所有服务
3. 根据当前轮询ID取余服务长度得到具体服务

## 工厂

# faultTol

容错策略实现

加入到SPI当中

## 快速失败

将错误返回给调用方

## 故障转移

从其他节点中获取一个, 替换当前节点

## 忽略错误

将当前所悟忽略, 不做处理

# RpcInvokerProxy


代理层

*  处理通信细节, 比如设置请求头, 请求体, 拦截器, 设置负载均衡, 拿到服务并处理异常
*  实现InvocationHandler, 完成动态代理

## 重写invoke, 完成动态代理

`proxy`：表示代理对象。

`method`：表示被代理的方法。

`args`：表示方法的参数列表。

### 构建消息, 消息头

1. 从ProtocolField中为消息头获取属性
2. 设置序列化方式
3. 构建请求体
4. 完成消息设置

### 拦截器

1. 拦截器拿到请求体数据
2. 添加到责任链中

```
FilterConfig.getClientBeforeFilterChain().doFilter(filterData);
//
```

### 负载均衡

在客户端完成

1. 为当前客户端服务构建key
2. 获取负载均衡策略
3. 根据策略获取到访问的服务
4. 获取到其他服务

### 重试机制

Future 表示一个异步计算的结果。

Promise 是一个更为灵活的概念，它表示一个异步任务的完成状态，并且可以用于设置任务的结果。

#### 容错策略

加入到spi中
