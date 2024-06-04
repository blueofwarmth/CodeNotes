package com.rpc.faultTol;

import com.rpc.common.RpcResponse;
import com.rpc.common.ServiceMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.Collection;

/**
 * @author Qyingli
 * @date 2024/5/22 15:05
 * @package: com.rpc.faultTol
 * @description: TODO 故障转移
 */
public class FailOver implements FaultTolerantStrategy{
    private Logger log = LoggerFactory.getLogger(FailOver.class);

    @Override
    public Object handler(Object... args) {
        String errorMsg = (String)args[2];
        int count = (int)args[3];
        ServiceMeta curServiceMeta = (ServiceMeta) args[0];
        Collection<ServiceMeta> otherServiceMeta = (Collection<ServiceMeta>) args[1];
        log.warn("rpc 调用失败,第{}次重试,异常信息:{}",count,errorMsg);
        //调用其他服务代替
        if (!ObjectUtils.isEmpty(otherServiceMeta)){
            final ServiceMeta next = otherServiceMeta.iterator().next();
            curServiceMeta = next;
            otherServiceMeta.remove(next);
        }else {
            final String msg = String.format("rpc 调用失败,无服务可用, 异常信息: {%s}", errorMsg);
            log.warn(msg);
            throw new RuntimeException(msg);
        }
        return null;
    }
}
