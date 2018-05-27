package org.snail.rocket.dispatcher.impl;

import org.snail.rocket.common.model.MessageStatus;
import org.snail.rocket.common.model.PushTaskMessage;
import org.snail.rocket.common.utils.JSONUtils;
import org.snail.rocket.dispatcher.AbstarctDispatcher;
import org.snail.rocket.strategy.RealTimeStrategy;
import org.snail.rocket.strategy.Strategy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 简单模式下的转发器实现
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-29 13:56
 */
public class CommonDispatcher extends AbstarctDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDispatcher.class);

    private Strategy defaultStrategy = new RealTimeStrategy();

    @Override
    public void dispatcherTask(PushTaskMessage pushTask) {
        Strategy strategy = null;
        String dispatcherType = pushTask.getDispatcherType();
        if(!CollectionUtils.isEmpty(dispatcherStrategys) && StringUtils.isNotBlank(dispatcherType)){
            strategy = dispatcherStrategys.get(dispatcherType);
            LOGGER.info(String.format("使用分发策略[%s]分发任务[%s],任务[%s]。", strategy.strategyName(),pushTask.getTaskName(), JSONUtils.toJSONString(pushTask)));
        }
        if(strategy == null) {
            strategy = defaultStrategy;
            LOGGER.info(String.format("未配置分发策略，使用默认的分发策略[%s]分发任务[%s],任务[%s]。", strategy.strategyName(),pushTask.getTaskName(),JSONUtils.toJSONString(pushTask)));
        }
        strategy.operate(pushTask);

        if(rocketListener != null && MessageStatus.DISPATCHERED.getVal().equals(pushTask.getStatus())) {
            rocketListener.notifyHandleTask(strategy.getConsumer());
            LOGGER.info(String.format("成功通知任务处理器开始处理任务[%s],任务[%s]。",pushTask.getTaskName(),JSONUtils.toJSONString(pushTask)));
        }

    }

    @Override
    public void dispatcherTask(String message) {
    }

}
