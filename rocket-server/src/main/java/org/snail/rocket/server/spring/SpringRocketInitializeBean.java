package org.snail.rocket.server.spring;

import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.common.utils.ConfigUtils;
import org.snail.rocket.common.utils.SpringUtils;
import org.snail.rocket.server.exception.RocketServerException;
import org.snail.rocket.trigger.database.BinlogDataListener;
import org.snail.rocket.trigger.database.worker.BaseBinlogDataWorker;
import org.snail.rocket.trigger.database.worker.DefaultBinlogDataWorker;
import org.snail.rocket.instance.RocketInstance;
import org.snail.rocket.instance.impl.SingleRocketInstanceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单模式下集成spring 的rocket初始化类
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-30 13:50
 */

public class SpringRocketInitializeBean extends SpringRocketAbstractInitializeBean
{
    private static Logger LOGGER = LoggerFactory.getLogger(SpringRocketInitializeBean.class);
    private Map<String, RocketInstance> rocketInstances = new ConcurrentHashMap<>();
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected BaseBinlogDataWorker getBinlogDataWorker(BinlogDataListener binlogDataListener) {
        BaseBinlogDataWorker simpleDataWorker = new DefaultBinlogDataWorker(
                binlogDataListener.getBinlogMessageQueue(), getDispatcher(),monitorDataKeeper,tableInfoKeeper);
        return simpleDataWorker;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            final ApplicationContext context = event.getApplicationContext();
            SpringUtils.context = context;
            if (StringUtils.isEmpty(rocketPropertiesPath)) {
                throw new RocketServerException("未找到配置文件！");
            }
            List<RocketConfig> rocketConfigs;
            try {
                rocketConfigs = ConfigUtils.getRocketInstancesConfig(rocketPropertiesPath, connectionProperties);
            } catch (Exception e) {
                LOGGER.error("加载配置文件失败！具体信息：" + e.getMessage(), e);
                throw new RocketServerException("加载配置文件失败！具体信息：" + e.getMessage());
            }
            SingleRocketInstanceGenerator rocketInstanceGenerator = new SingleRocketInstanceGenerator();
            for (RocketConfig rocketConfig : rocketConfigs) {
                RocketInstance singleRocketInstance = rocketInstanceGenerator.generateSingle(rocketConfig, dispatcher);
                singleRocketInstance.start();
                rocketInstances.put(rocketConfig.getInstanceId(), singleRocketInstance);
            }
        }
    }
}
