package org.snail.rocket.server;

import org.snail.rocket.common.RocketLifeCycle;
import org.snail.rocket.server.exception.RocketServerException;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-03 17:10
 */

public interface RocketServer extends RocketLifeCycle {
    @Override
    void start() throws RocketServerException;

    @Override
    void stop() throws RocketServerException;
}
