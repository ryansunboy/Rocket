package org.snail.rocket.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shouchen21647
 */
public class MQDefaultConsumer extends AbstractConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MQDefaultConsumer.class);

    public MQDefaultConsumer(ExecutorService service,MessageHandler messageHandler) {
        super(service,messageHandler);
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
    }

}
