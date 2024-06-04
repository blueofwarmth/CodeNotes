package com.rpc.consumer;

import com.rpc.common.RpcRequest;
import com.rpc.common.ServiceMeta;
import com.rpc.protocol.RpcProtocol;
import com.rpc.protocol.codec.RpcDecoder;
import com.rpc.protocol.codec.RpcEncoder;
import com.rpc.protocol.handler.consumer.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qyingli
 * @date 2024/5/15 15:15
 * @package: com.rpc.consumer
 * @description: TODO 消费方发送数据
 * 1. 配置netty处理数据并响应
 * 2. 发送数据
 */
public class RpcConsumer {

    private final Bootstrap bootstrap;
//    用于管理和调度一组 EventLoop。它负责处理所有的 I/O 操作、任务调度以及事件分发。
    // note boss: 处理客户端连接  wroker:处理i/o事件
    private final EventLoopGroup eventLoopGroup;
    private Logger logger = LoggerFactory.getLogger(RpcConsumer.class);

    //netty配置
    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //编解码, 响应
                        socketChannel.pipeline()
                                .addLast(new RpcDecoder())
                                .addLast(new RpcEncoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    /**
     * 建立连接, 发送请求
     * @param protocol 消息
     * @param serviceMetadata 服务
     * @return 当前服务
     * @throws Exception
     */
    public void sendRequest(RpcProtocol<RpcRequest> protocol, ServiceMeta serviceMetadata) throws Exception {

        if (serviceMetadata != null) {
            String address = serviceMetadata.getServiceAddr();
            int port = serviceMetadata.getServicePort();

            // 连接, 得到结果
            ChannelFuture future = bootstrap.connect(address, port).sync();
            //添加事件, 拿到响应结果
            future.addListener((ChannelFutureListener) arg0 -> {
                if (future.isSuccess()) {
                    logger.info("连接 rpc server {} 端口 {} 成功.", address, port);
                } else {
                    logger.error("连接 rpc server {} 端口 {} 失败.", address, port);
                    future.cause().printStackTrace();
                    eventLoopGroup.shutdownGracefully();
                }
            });
            // 写入数据
            future.channel().writeAndFlush(protocol);
        }
    }

}
