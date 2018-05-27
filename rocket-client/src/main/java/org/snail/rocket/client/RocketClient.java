package org.snail.rocket.client;

import org.snail.rocket.client.exception.RocketTimeOutException;
import org.snail.rocket.client.netty.Connection;
import org.snail.rocket.client.netty.ConnectionFactory;
import org.snail.rocket.client.netty.NettyClientHandler;
import org.snail.rocket.client.netty.SyncFuture;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.message.AskMsg;
import org.snail.rocket.message.AskParams;
import org.snail.rocket.message.LogOutMsg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * rocket 通用客户端
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-07 19:56
 */

public class RocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketClient.class);
    private ConnectionFactory connectionFactory;
    private Connection connection;

    public Message recieveMessage(String groupType) throws InterruptedException,RocketTimeOutException {
        if(connection == null){
            connection = connectionFactory.createConnection();
            int i = 0;
            while (StringUtils.isBlank(connection.getNettyClientHandler().getClientId())){
                LOGGER.info("waiting for server auth of client.");
                Thread.sleep(100);
                i++;
                if(i>50){
                    throw new RocketTimeOutException("connecting timeout...");
                }
            }
        }
        String requestId = UUID.randomUUID().toString();
        AskMsg askMsg=new AskMsg(connection.getNettyClientHandler().getClientId(),requestId,groupType);
        AskParams askParams=new AskParams();
        askParams.setAuth(connection.getNettyClientHandler().getAuth());
        askParams.setRequestId(requestId);
        askMsg.setParams(askParams);
        connection.getSocketChannel().writeAndFlush(askMsg);
        SyncFuture<Message> future = new SyncFuture<>();
        NettyClientHandler.futureMap.put(requestId,future);
        Message message = future.get(10000, TimeUnit.MILLISECONDS);
        return message;
    }

    public void close(){
        LogOutMsg logOutMsg = new LogOutMsg(connection.getNettyClientHandler().getClientId());
        connection.getSocketChannel().writeAndFlush(logOutMsg);
        connectionFactory.getEventLoopGroup().shutdownGracefully();
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
