package com.caoyx.rpc.core.net.netty.server;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.net.api.Server;
import com.caoyx.rpc.core.net.netty.codec.CaoyxRpcDecoder;
import com.caoyx.rpc.core.net.netty.codec.CaoyxRpcEncoder;
import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import com.caoyx.rpc.core.utils.ThrowableUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @author caoyixiong
 */
@Slf4j
public class NettyServer implements Server {
    private EventLoopGroup receiveGroup;
    private EventLoopGroup workGroup;
    private NettyServerHandler serverHandler;

    public void start(final int port, final CaoyxRpcProviderFactory caoyxRpcProviderFactory) throws CaoyxRpcException {
        receiveGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        serverHandler = new NettyServerHandler(caoyxRpcProviderFactory);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(receiveGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 32 * 1024 * 1024)
                .option(ChannelOption.SO_SNDBUF, 32 * 1024 * 1024) // 发送缓冲区
                .option(ChannelOption.SO_RCVBUF, 32 * 1024 * 1024) // 接收缓冲区
                .option(ChannelOption.SO_KEEPALIVE, true)   // 保持长连接
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 10, 4))
                                .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
                                .addLast(new CaoyxRpcDecoder(CaoyxRpcRequest.class))
                                .addLast(new CaoyxRpcEncoder())
                                .addLast(serverHandler);
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("server start success, port:" + port);
                    } else {
                        log.error("server start fail, errorMsg: " + ThrowableUtils.throwable2String(future.cause()));
                    }
                }
            });
        } catch (InterruptedException e) {
            shutdownGracefully();
            throw new CaoyxRpcException(e);
        }
    }

    @Override
    public void shutdownGracefully() {
        if (serverHandler != null) {
            serverHandler.shutdownGracefully();
        }
        if (receiveGroup != null) {
            receiveGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }
}