package com.rpc.router;

import com.rpc.common.ServiceMeta;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Qyingli
 * @date 2024/5/21 20:27
 * @package: com.rpc.router
 * @description: TODO
 */
@Data
public class LoadBalenceMeta {

        // 当前服务节点
        private ServiceMeta curServiceMeta;
        // 剩余服务节点
        private Collection<ServiceMeta> otherServiceMeta;

        //构建负载均衡节点
        public static LoadBalenceMeta build(ServiceMeta curServiceMeta, Collection<ServiceMeta> otherServiceMeta){
            final LoadBalenceMeta serviceMetaRes = new LoadBalenceMeta();
            serviceMetaRes.curServiceMeta = curServiceMeta;
            // 如果只有一个服务
            if(otherServiceMeta.size() == 1){
                otherServiceMeta = new ArrayList<>();
            } else {
                //移除当前服务
                otherServiceMeta.remove(curServiceMeta);
            }

            serviceMetaRes.otherServiceMeta = otherServiceMeta;
            return serviceMetaRes;
        }
}
