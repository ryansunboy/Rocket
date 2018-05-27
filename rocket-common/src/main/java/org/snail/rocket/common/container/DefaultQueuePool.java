package org.snail.rocket.common.container;

import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.model.MessageStatus;
import org.snail.rocket.common.model.PushTaskMessage;
import org.snail.rocket.common.utils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
* @Description: 默认处理队列
* @author liuyang下午3:23:33DefaultQueuePool.java
* @date 2018年1月16日 下午3:23:33
*
 */
public class DefaultQueuePool implements QueuePool
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueuePool.class);
    static LinkedBlockingQueue<Message> queuePool = new LinkedBlockingQueue<Message>();

    @Override
    public void put(PushTaskMessage message)
    {
  
        try
        {
            queuePool.put(ConvertUtils.convertToMessage(message));
            message.setStatus(MessageStatus.DISPATCHERED.getVal());
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
        
   

    @Override
    public Message take()
    {
         try
        {
            return queuePool.take();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }
    
 
}
