package org.snail.rocket.client.netty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.snail.rocket.message.LoginMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-07 11:16
 */

public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private int port;
    private String host;
    private String username;
    private String password;
    private EventLoopGroup eventLoopGroup;
    public ConnectionFactory(int port, String host,String username,String password){
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
    }
    public Connection createConnection() throws InterruptedException {
        Connection connection = new Connection();
        final NettyClientHandler nettyClientHandler = new NettyClientHandler();
        eventLoopGroup=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(host,port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new IdleStateHandler(20,10,0));
                socketChannel.pipeline().addLast(new ObjectEncoder());
                socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                socketChannel.pipeline().addLast(nettyClientHandler);
            }
        });
        ChannelFuture future =bootstrap.connect(host,port).sync();
        connection.setNettyClientHandler(nettyClientHandler);
        if (future.isSuccess()) {
            SocketChannel socketChannel = (SocketChannel)future.channel();
            LOGGER.info("connect server successful");
            connection.setSocketChannel(socketChannel);
            LoginMsg loginMsg=new LoginMsg();
            loginMsg.setPassword(password);
            loginMsg.setUserName(username);
            socketChannel.writeAndFlush(loginMsg);
        }
        return connection;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

}