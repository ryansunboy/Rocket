package org.snail.rocket.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
* @Description: MQ生产者
* @author liuyang下午3:23:13DefaultProducer.java
* @date 2018年1月16日 下午3:23:13
*
 */
public class MQDefaultProducer extends AbstractProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MQDefaultProducer.class);

    public MQDefaultProducer(ExecutorService service, MessageHandler messageHandler) {
        super(service,messageHandler);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }


    @Override
    public void init()
    {
        if(service == null) {
            service = Executors.newCachedThreadPool();
        }
    }
}
