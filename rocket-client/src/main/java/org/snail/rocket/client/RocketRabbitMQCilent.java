package org.snail.rocket.client;

import org.snail.rocket.client.exception.RocketClientException;
import org.snail.rocket.common.container.MessageHandler;
import org.slf4j.LoggerFactory;

/**
 * rocket MQ模式客户端
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-29 15:45
 */

public class RocketRabbitMQCilent  {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(RocketRabbitMQCilent.class);
    private MessageHandler messageHandler;

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public String getPushTaskWithoutAck(){
        String message;
        try {
            Object o = messageHandler.receiveDataFromQueue();
            if(o!=null && o instanceof String){
                message = o.toString();
            } else  {
                throw new RocketClientException("unsupport class type!");
            }
        } catch (Exception e) {
            Logger.error("get pushtask error!",e);
            throw new RocketClientException("get pushtask error!");
        }
        return message;
    }


}