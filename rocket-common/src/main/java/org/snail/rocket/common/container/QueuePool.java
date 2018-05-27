package org.snail.rocket.common.container;

import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.model.PushTaskMessage;

/**
 * 
* @Description: 处理队列接口
* @author liuyang下午3:24:10QueuePool.java
* @date 2018年1月16日 下午3:24:10
*
 */
public interface QueuePool
{
    public void put(PushTaskMessage message);
    
    public Message take();
}
