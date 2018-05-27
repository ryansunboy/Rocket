package org.snail.rocket.strategy;

import org.snail.rocket.common.container.DelayConsumer;
import org.snail.rocket.common.container.DelayProducer;
import org.snail.rocket.common.container.DelayQueuePool;
import org.snail.rocket.common.container.QueuePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-08 14:31
 */

public class DelayTimeStrategy extends AbstractStrategy{
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayTimeStrategy.class);
    private static final String STRATEGYNAME = "DelayStrategy";


    @Override
    public void init() {
        QueuePool queuePool = new DelayQueuePool();
        producer =  new DelayProducer(producerWorkerThreadPool,queuePool);
        consumer = new DelayConsumer(consumerWorkerThreadPool,queuePool);
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
