package com.rpc.common.constants;

public interface LoadBalancerRules {

    //一致性哈希
    String ConsistentHash = "consistentHash";
    //轮询
    String RoundRobin = "roundRobin";
}