package org.snail.rocket.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
* @Description: 默认生产者
* @author liuyang下午3:23:13DefaultProducer.java
* @date 2018年1月16日 下午3:23:13
*
 */
public class DefaultProducer extends AbstractProducer
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProducer.class);

    public DefaultProducer(ExecutorService service, QueuePool queuePool) {
        super(service, queuePool);
    }


    @Override
    public void init()
    {
        if(service == null) {
            service = Executors.newCachedThreadPool();
        }
        if(queuePool == null) {
            queuePool = new DefaultQueuePool();
        }
    }


    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
