package org.snail.rocket.strategy;

import org.snail.rocket.common.container.Consumer;
import org.snail.rocket.common.container.Producer;
import org.snail.rocket.common.model.PushTaskMessage;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-08 14:33
 */

public interface Strategy {
    void operate(PushTaskMessage pushTask);
    void operate(String message);
    Consumer getConsumer();
    Producer getProducer();
    String strategyName();
}
