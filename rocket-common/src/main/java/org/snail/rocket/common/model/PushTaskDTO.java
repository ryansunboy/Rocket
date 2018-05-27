package org.snail.rocket.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务传输实体
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-02 13:27
 */

public class PushTaskDTO implements Serializable {
    private static final long serialVersionUID = 7265086157052590332L;
    private String taskUUID;

    private Map<String,String> params = new HashMap<>();

    private String taskClass;

    private String taskName;

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
