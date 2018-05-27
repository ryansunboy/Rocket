package org.snail.rocket.common.container;

import org.snail.rocket.common.model.PushTaskMessage;
/**
 * 
* @Description: 消费者接口
* @author liuyang下午3:23:53Producer.java
* @date 2018年1月16日 下午3:23:53
*
 */
public interface Producer extends BaseContainer
{
    public void produce(PushTaskMessage message);
}
