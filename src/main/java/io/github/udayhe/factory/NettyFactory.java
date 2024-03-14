package io.github.udayhe.factory;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.inject.Singleton;


/**
 * @author udayhegde
 */
@Factory
public class NettyFactory {

    @Bean
    @Singleton
    public ServerBootstrap nettyServerBootstrap(ChannelInitializer<SocketChannel> channelInitializer) {
        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer);

        return serverBootstrap;
    }

    @Bean
    @Singleton
    public ChannelInitializer<SocketChannel> channelInitializer(ChannelGroup channelGroup) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new MyHandler(channelGroup));
            }
        };
    }

    class MyHandler extends ChannelInboundHandlerAdapter {
        private final ChannelGroup channelGroup;

        public MyHandler(ChannelGroup channelGroup) {
            this.channelGroup = channelGroup;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            channelGroup.add(ctx.channel());
            System.out.println("New connection added. Total active connections: " + channelGroup.size());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            channelGroup.remove(ctx.channel());
            System.out.println("Connection closed. Total active connections: " + channelGroup.size());
        }
    }
}
