package org.snail.rocket.trigger.database.handletask;

import org.snail.rocket.common.model.MessageStatus;
import org.snail.rocket.common.model.PushTaskMessage;
import org.snail.rocket.common.utils.DateUtil;
import org.snail.rocket.dispatcher.Dispatcher;
import org.snail.rocket.trigger.database.BinlogMessage;
import org.snail.rocket.trigger.support.MonitorDataKeeper;
import org.snail.rocket.trigger.support.RocketTaskInfo;
import org.snail.rocket.trigger.support.TableInfoKeeper;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * 简单模式下的binlog 处理器
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-28 16:17
 */

public class DefaultBinlogDataHandleTask extends AbstractBinlogDataHandleTask {
    public DefaultBinlogDataHandleTask() {
    }

    public  DefaultBinlogDataHandleTask(Dispatcher dispatcher, BinlogMessage binlogMessage, MonitorDataKeeper monitorDataKeeper, TableInfoKeeper tableInfoKeeper) {
        super(dispatcher,binlogMessage,monitorDataKeeper,tableInfoKeeper);
    }

    /**
     * @Title  DefaultBinlogDataHandleTask.java
     * @Package net.hs.rocket.trigger.database.handletask
     * @Description 将处理完成的数据通过简单分发器分发
     * @Author shouchen21647@hundsun.com
     * @Date 2018-01-09 13:41
     * @Params
     * @param rocketTaskInfo
     * @param taskParam
     * @return void
     */
    @Override
    public void dispatcherTask(RocketTaskInfo rocketTaskInfo, Map<String,String> taskParam) {
        PushTaskMessage pushTask = new PushTaskMessage();
        pushTask.setParams(taskParam);
        pushTask.setTaskMessage(rocketTaskInfo.getTaskMessage());
        pushTask.setTaskName(rocketTaskInfo.getTaskDescription());
        pushTask.setTaskCode(rocketTaskInfo.getTaskCode());
        pushTask.setDispatcherType(rocketTaskInfo.getTaskType());
        pushTask.setTriggerSource(rocketTaskInfo.getTriggerSource());
        pushTask.setInstanceId(rocketTaskInfo.getInstanceId());
        if (StringUtils.isNotBlank(rocketTaskInfo.getTaskDelayTime())) {
            pushTask.setDelay(Long.valueOf(rocketTaskInfo.getTaskDelayTime()) * 1000);
        } else {
            pushTask.setDelay(0);
        }
        pushTask.setTaskDescription(rocketTaskInfo.getTaskDescription());
        pushTask.setCreatedAt(DateUtil.DateToString(new Date(), DateUtil.DateStyle.YYYY_MM_DD_HH_MM_SS));
        pushTask.setStatus(MessageStatus.UNDISPATCHER.getVal());
        getDispatcher().dispatcherTask(pushTask);
    }
}