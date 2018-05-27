package org.snail.rocket.listener;

import org.snail.rocket.common.container.Consumer;

/**
 * 
* @Description: 默认任务监听器
* @author liuyang下午1:51:23DefaultTaskListener.java
* @date 2018年1月11日 下午1:51:23
*
 */
public class DefaultTaskListener implements RocketListener
{

    @Override
    public void notifyHandleTask(Consumer consumer)
    {
        consumer.consume();
        
    }


  
}
