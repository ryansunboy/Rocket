package org.snail.rocket.common;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-03 17:04
 */

public interface RocketLifeCycle {
    void start();

    void stop();

    boolean isStart();
}
