package org.snail.rocket.instance.impl;

import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.dispatcher.Dispatcher;
import org.snail.rocket.instance.RocketInstance;
import org.snail.rocket.instance.RocketInstanceGenerator;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-25 16:03
 */

public class SingleRocketInstanceGenerator implements RocketInstanceGenerator {


    @Override
    public RocketInstance generate(String destination) {
        return null;
    }

    @Override
    public RocketInstance generateSingle(RocketConfig rocketConfig,Dispatcher dispatcher) {
        return new SingleRocketInstance(rocketConfig,dispatcher);
    }



}
