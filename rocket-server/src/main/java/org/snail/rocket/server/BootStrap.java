package org.snail.rocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.common.utils.ConfigUtils;
import org.snail.rocket.server.exception.RocketServerException;
import org.snail.rocket.server.impl.SingleRocketServer;
import org.snail.rocket.server.handler.NettyServerHandler;
import org.snail.rocket.instance.impl.SingleRocketInstanceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-25 15:57
 */

public class BootStrap {
    private static final Logger logger = LoggerFactory.getLogger(BootStrap.class);
    private int port;
    private SocketChannel socketChannel;

    public BootStrap(int port) throws InterruptedException {
        this.port = port;
        bind();
    }

    private void bind() throws InterruptedException {
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();
        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(boss,worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        //保持长连接状态
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new ObjectEncoder());
                p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                p.addLast(new NettyServerHandler());
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            logger.info("server start");
        }
    }
    public static void main(String[] args) throws InterruptedException {
        List<RocketConfig> rocketConfigs;
        try {
           rocketConfigs = ConfigUtils.getRocketInstancesConfig(args[0],"config.decrypt=true");
        } catch (Exception e) {
            logger.error("加载配置文件失败！具体信息："+ e.getMessage(),e);
            throw new RocketServerException("加载配置文件失败！具体信息："+ e.getMessage());
        }
        SingleRocketInstanceGenerator rocketInstanceGenerator = new SingleRocketInstanceGenerator();
        SingleRocketServer singleRocketServer = SingleRocketServer.instance();
        singleRocketServer.setRocketInstanceGenerator(rocketInstanceGenerator);
        singleRocketServer.setRocketConfigs(rocketConfigs);
        singleRocketServer.start();
        BootStrap bootstrap=new BootStrap(28800);
    }

}
