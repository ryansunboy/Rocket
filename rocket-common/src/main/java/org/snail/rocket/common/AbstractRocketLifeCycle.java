package org.snail.rocket.common;

import org.snail.rocket.common.exception.RocketException;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-03 17:05
 */

public abstract class AbstractRocketLifeCycle implements RocketLifeCycle{
    protected volatile boolean running = false; // 是否处于运行中

    @Override
    public boolean isStart() {
        return running;
    }

    @Override
    public void start() {
        if (running) {
            throw new RocketException(this.getClass().getName() + " has startup , don't repeat start");
        }

        running = true;
    }

    @Override
    public void stop() {
        if (!running) {
            throw new RocketException(this.getClass().getName() + " isn't start , please check");
        }

        running = false;
    }
}
