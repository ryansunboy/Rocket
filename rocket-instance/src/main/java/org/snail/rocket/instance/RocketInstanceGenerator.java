package org.snail.rocket.instance;

import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.dispatcher.Dispatcher;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-25 15:09
 */

public interface RocketInstanceGenerator {
    RocketInstance generate(String destination);
    RocketInstance generateSingle(RocketConfig rocketConfig, Dispatcher dispatcher);
}
