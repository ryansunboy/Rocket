package org.snail.rocket.client.netty;

import io.netty.channel.socket.SocketChannel;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-07 14:29
 */

public class Connection {
    private SocketChannel socketChannel;
    private NettyClientHandler nettyClientHandler;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public NettyClientHandler getNettyClientHandler() {
        return nettyClientHandler;
    }

    public void setNettyClientHandler(NettyClientHandler nettyClientHandler) {
        this.nettyClientHandler = nettyClientHandler;
    }
}
