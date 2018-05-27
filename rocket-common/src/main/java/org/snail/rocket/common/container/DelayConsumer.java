package org.snail.rocket.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @Description: 默认消费者
 * @author liuyang下午3:22:53DefaultConsumer.java
 * @date 2018年1月16日 下午3:22:53
 *
 */
public class DelayConsumer extends AbstractConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayConsumer.class);

    public DelayConsumer(ExecutorService service,QueuePool queuePool) {
        super(service, queuePool);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void init() {
        if(service == null) {
            service = Executors.newCachedThreadPool();
        }
        if(queuePool == null) {
            queuePool = new DelayQueuePool();
        }
    }

}
