package org.snail.rocket.common.container;

import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.model.DelayFlag;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.model.MessageStatus;
import org.snail.rocket.common.model.PushTaskMessage;
import org.snail.rocket.common.utils.ConvertUtils;
import org.snail.rocket.common.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * 
* @Description: 默认处理队列
* @author liuyang下午3:23:33DefaultQueuePool.java
* @date 2018年1月16日 下午3:23:33
*
 */
public class DelayQueuePool implements QueuePool
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayQueuePool.class);

    static DelayQueue<Message> queuePool = new DelayQueue<>();
    static Map<String,DelayFlag> uniqueMap = new ConcurrentHashMap<>();

    @Override
    public void put(PushTaskMessage message) {
        String taskCode = message.getTaskCode();
        if(StringUtils.isBlank(taskCode)){
            throw new RocketException("任务编号为空！请检查配置！");
        }
        String param = JSONUtils.toJSONString(CollectionUtils.isEmpty(message.getParams())?"":message.getParams());
        String uniqueKey = message.getInstanceId() + "@" + taskCode + "@" + param;
        DelayFlag delayFlag = uniqueMap.get(uniqueKey);
        if(delayFlag == null){
            synchronized (this) {
                if (delayFlag == null) {
                    delayFlag = new DelayFlag();
                    uniqueMap.put(uniqueKey, delayFlag);
                }
            }
        }
        synchronized (delayFlag) {
            if (false == delayFlag.getFlag().get() || System.currentTimeMillis() - delayFlag.getDate() > message.getDelay()) {
                putMessageTpQueue(message, delayFlag);
                LOGGER.info(String.format("消息存入队列，消息内容[%s]", JSONUtils.toJSONString(message)));
            } else if(System.currentTimeMillis() - delayFlag.getDate() > message.getDelay()){
                LOGGER.warn(String.format("队列任务消费时间大于延迟时间，消息存入队列。未消费任务消息[%s]",JSONUtils.toJSONString(delayFlag.getMessage())));
                putMessageTpQueue(message, delayFlag);
                LOGGER.info(String.format("消息存入队列，消息内容[%s]", JSONUtils.toJSONString(message)));
            } else {
                LOGGER.warn(String.format("队列中已经存在相同的任务消息，丢弃该消息[%s]，队列长度[%s]", JSONUtils.toJSONString(message),queuePool.size()));
                message.setStatus(MessageStatus.DISCARD.getVal());
            }
        }
    }

    private void putMessageTpQueue(PushTaskMessage message, DelayFlag delayFlag) {
        message.setDelay(message.getDelay() + System.currentTimeMillis());
        queuePool.put(ConvertUtils.convertToMessage(message));
        delayFlag.setDate(System.currentTimeMillis());
        delayFlag.getFlag().set(true);
        delayFlag.setMessage(message);
        message.setStatus(MessageStatus.DISPATCHERED.getVal());
    }


    @Override
    public Message take()
    {
         try
        {
            Message message = queuePool.take();
            String taskCode = message.getTaskCode();
            String param = JSONUtils.toJSONString(CollectionUtils.isEmpty(message.getParams())?"":message.getParams());
            String uniqueKey = message.getInstanceId() + "@" + taskCode + "@" + param;
            DelayFlag delayFlag = uniqueMap.get(uniqueKey);
            synchronized (delayFlag) {
                delayFlag.getFlag().set(false);
                delayFlag.setMessage(null);
                LOGGER.info(String.format("消息出队列，消息内容[%s]，队列长度[%s]", JSONUtils.toJSONString(message),queuePool.size()));
            }
            return message;
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }
    
 
}
