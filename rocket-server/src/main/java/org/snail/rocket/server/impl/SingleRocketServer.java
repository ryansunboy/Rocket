package org.snail.rocket.server.impl;

import org.snail.rocket.common.AbstractRocketLifeCycle;
import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.dispatcher.impl.CommonDispatcher;
import org.snail.rocket.server.RocketServer;
import org.snail.rocket.server.exception.RocketServerException;
import org.snail.rocket.strategy.DelayTimeStrategy;
import org.snail.rocket.strategy.RealTimeStrategy;
import org.snail.rocket.strategy.Strategy;
import org.snail.rocket.instance.RocketInstance;
import org.snail.rocket.instance.RocketInstanceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-09 19:23
 */

public class SingleRocketServer extends AbstractRocketLifeCycle implements RocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SingleRocketServer.class);
    private Map<String, RocketInstance> rocketInstances = new ConcurrentHashMap<>();
    private RocketInstanceGenerator rocketInstanceGenerator;
    private List<RocketConfig> rocketConfigs;

    private static class SingletonHolder {
        private static final SingleRocketServer CANAL_SERVER_WITH_SINGLE = new SingleRocketServer();
    }

    public SingleRocketServer(){
        // 希望也保留用户new单独实例的需求,兼容历史
    }

    public static SingleRocketServer instance() {
        return SingletonHolder.CANAL_SERVER_WITH_SINGLE;
    }

    @Override
    public void start() {
        if(CollectionUtils.isEmpty(rocketConfigs)){
            throw new RocketServerException("配置文件为空！");
        }
        if (!isStart()) {
            super.start();
            DelayTimeStrategy delayTimeStrategy = new DelayTimeStrategy();
            RealTimeStrategy realTimeStrategy = new RealTimeStrategy();
            Map<String,Strategy> strategyMap = new HashMap<>();
            strategyMap.put("delay",delayTimeStrategy);
            strategyMap.put("realTime",realTimeStrategy);
            CommonDispatcher dispatcher = new CommonDispatcher();
            dispatcher.setDispatcherStrategys(strategyMap);
            for(RocketConfig rocketConfig : rocketConfigs){
                RocketInstance singleRocketInstance =  rocketInstanceGenerator.generateSingle(rocketConfig,dispatcher);
                singleRocketInstance.start();
                rocketInstances.put(rocketConfig.getInstanceId(),singleRocketInstance);
            }
        }
    }

    public RocketInstanceGenerator getRocketInstanceGenerator() {
        return rocketInstanceGenerator;
    }

    public void setRocketInstanceGenerator(RocketInstanceGenerator rocketInstanceGenerator) {
        this.rocketInstanceGenerator = rocketInstanceGenerator;
    }

    public List<RocketConfig> getRocketConfigs() {
        return rocketConfigs;
    }

    public void setRocketConfigs(List<RocketConfig> rocketConfigs) {
        this.rocketConfigs = rocketConfigs;
    }

    public void init(String path){

    }
}
