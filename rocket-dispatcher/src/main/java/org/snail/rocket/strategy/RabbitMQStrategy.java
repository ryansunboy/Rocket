package org.snail.rocket.strategy;

import org.snail.rocket.common.container.MQDefaultConsumer;
import org.snail.rocket.common.container.MQDefaultProducer;
import org.snail.rocket.common.container.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-08 14:31
 */

public class RabbitMQStrategy extends AbstractStrategy{
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQStrategy.class);
    private static final String STRATEGYNAME = "RabbitMQStrategy";
    private MessageHandler messageHandler;

    @Override
    public void init() {
        producer =  new MQDefaultProducer(producerWorkerThreadPool,messageHandler);
        consumer = new MQDefaultConsumer(consumerWorkerThreadPool,messageHandler);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String strategyName() {
        return STRATEGYNAME;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
