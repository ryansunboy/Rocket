package org.snail.rocket.common.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 任务实体
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-29 14:00
 */

public class Message implements Delayed,Serializable{
    private static final long serialVersionUID = 2560616824077021081L;

    private String instanceId;

    private Map<String,String> params = new HashMap<>();

    private String taskMessage;

    private String taskName;

    private String taskCode;

    private String taskDescription;

    private long delay;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

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

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }


    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long r = delay - Calendar.getInstance().getTimeInMillis();
        return r;
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.delay - o.getDelay(TimeUnit.MILLISECONDS));
    }
}