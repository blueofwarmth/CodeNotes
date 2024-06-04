package com.rpc.common.constants;


public interface FaultTolerantRules {
    //故障转移
    String Failover = "failover";
    //快速失败
    String FailFast = "failFast";
    //失效保护
    String Failsafe = "failsafe";
}