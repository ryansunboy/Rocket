package org.snail.rocket.strategy;

import org.snail.rocket.common.container.DefaultConsumer;
import org.snail.rocket.common.container.DefaultProducer;
import org.snail.rocket.common.container.DefaultQueuePool;
import org.snail.rocket.common.container.QueuePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-08 14:31
 */

public class RealTimeStrategy extends AbstractStrategy{
    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeStrategy.class);
    private static final String STRATEGYNAME = "RealStrategy";

    @Override
    public void init() {
        QueuePool queuePool = new DefaultQueuePool();
        producer =  new DefaultProducer(producerWorkerThreadPool,queuePool);
        consumer = new DefaultConsumer(consumerWorkerThreadPool,queuePool);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String strategyName() {
        return STRATEGYNAME;
    }
}
