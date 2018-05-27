package org.snail.rocket.common.container;

/**
 * rabbitMQ 消息发送器
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-29 15:27
 */

import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.model.PushTaskMessage;
import org.snail.rocket.common.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;

public class MessageHandler implements QueuePool {

    private Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private AmqpTemplate amqpTemplate;

    private String routingKey;

    private String queueName;

    public AmqpTemplate getAmqpTemplate() {
        return amqpTemplate;
    }

    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void sendDataToQueue(Object obj) {
        logger.info("to send message:{}", obj);
        try {
            amqpTemplate.convertAndSend(this.routingKey, obj);
        } catch (Exception e) {
            logger.error("发送异常", e);
        }
    }

    public Object receiveDataFromQueue() {
        try {
            return amqpTemplate.receiveAndConvert(this.queueName);
        } catch (Exception e) {
            logger.error("发送异常", e);
        }
        return null;
    }

    @Override
    public void put(PushTaskMessage message) {
        sendDataToQueue(JSONUtils.toJSONString(message));
    }

    @Override
    public Message take() {
        String message;
        try {
            Object o = receiveDataFromQueue();
            if (o != null && o instanceof String) {
                message = o.toString();
            } else {
                throw new RocketException("unsupport class type!");
            }
        } catch (Exception e) {
            logger.error("get pushtask error!", e);
            throw new RocketException("get pushtask error!");
        }
        if (StringUtils.isNotBlank(message)) {
            Message pushTaskMessage = JSONUtils.parse(message, Message.class);
            if (StringUtils.isBlank(pushTaskMessage.getTaskMessage())) {
                logger.info(String.format("-------------无效的任务----------"));
                return null;
            }
            return pushTaskMessage;
        }
        return null;
    }
}

