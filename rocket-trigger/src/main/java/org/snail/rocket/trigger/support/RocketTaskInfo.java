package org.snail.rocket.trigger.support;

import org.snail.rocket.common.model.Condition;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-31 14:43
 */

public class RocketTaskInfo {
    private String instanceId;

    private String taskMessage;

    private String taskName;

    private String taskCode;

    private String taskDescription;

    private String taskType;

    //延时任务的延时时间，实时任务可以没有该属性
    private String taskDelayTime;

    private String triggerSource;
    private List<Condition> conditions;

    public String getTaskMessage() {
        return taskMessage;
    }

    public void setTaskMessage(String taskMessage) {
        this.taskMessage = taskMessage;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskDelayTime() {
        return taskDelayTime;
    }

    public void setTaskDelayTime(String taskDelayTime) {
        this.taskDelayTime = taskDelayTime;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public String getTriggerSource() {
        return triggerSource;
    }

    public void setTriggerSource(String triggerSource) {
        this.triggerSource = triggerSource;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
