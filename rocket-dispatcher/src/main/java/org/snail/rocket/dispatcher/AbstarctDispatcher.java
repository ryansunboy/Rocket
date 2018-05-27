package org.snail.rocket.dispatcher;


import org.snail.rocket.listener.RocketListener;
import org.snail.rocket.strategy.Strategy;

import java.util.Map;

/**
 * rocket 转发器抽象类（用于通用扩展）
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-29 13:56
 */

public abstract class AbstarctDispatcher implements Dispatcher {
    protected RocketListener rocketListener;

    protected Map<String,Strategy> dispatcherStrategys;


    public RocketListener getRocketListener() {
        return rocketListener;
    }

    public void setRocketListener(RocketListener rocketListener) {
        this.rocketListener = rocketListener;
    }

    public Map<String, Strategy> getDispatcherStrategys() {
        return dispatcherStrategys;
    }

    public void setDispatcherStrategys(Map<String, Strategy> dispatcherStrategys) {
        this.dispatcherStrategys = dispatcherStrategys;
    }
}
