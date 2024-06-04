package com.rpc.protocol.handler.service;

import com.rpc.common.RpcRequest;
import com.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * @description: 处理消费方发送数据并且调用方法
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    public RpcRequestHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ThreadPollFactory.submitRequest(ctx,protocol);
    }

}

