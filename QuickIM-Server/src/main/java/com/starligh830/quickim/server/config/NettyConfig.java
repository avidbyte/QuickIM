package com.starligh830.quickim.server.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @author Aaron
 * @since 1.0
 */
@Configuration
public class NettyConfig {

    @Value("${quick-im.port}")
    private int port;

    @Bean(name = "nettyServerBootstrap")
    public ServerBootstrap nettyServerBootstrap(@Autowired ChannelInitializer<SocketChannel> channelInitializer) {
        // 创建 ServerBootstrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // 设置 EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        serverBootstrap.group(bossGroup, workerGroup);

        // 设置 Channel
        serverBootstrap.channel(NioServerSocketChannel.class);

        // 设置 ChildHandler
        serverBootstrap.childHandler(channelInitializer);

        // 设置其他参数
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        // 绑定端口并启动服务
        ChannelFuture future = serverBootstrap.bind(port);
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                System.out.println("Netty server started on port " + port);
            } else {
                System.err.println("Netty server start failed on port " + port);
            }
        });

        return serverBootstrap;
    }

}
