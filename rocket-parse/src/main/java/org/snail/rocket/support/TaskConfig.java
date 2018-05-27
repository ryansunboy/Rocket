package org.snail.rocket.support;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-29 14:00
 */
public class TaskConfig {

    private String taskName;
    
    private String taskCode;
    
    private String taskDescription;
    
    private String taskClass;

    private String taskType;

    private String taskDelayTime;

    private List<TableConfig> tableConfigs = new ArrayList<>();

   private List<TaskParamConfig> taskParamConfigs = Lists.newArrayList();

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public List<TableConfig> getTableConfigs() {
        return tableConfigs;
    }

    public void setTableConfigs(List<TableConfig> tableConfigs) {
        this.tableConfigs = tableConfigs;
    }

    public List<TaskParamConfig> getTaskParamConfigs() {
        return taskParamConfigs;
    }

    public void setTaskParamConfigs(List<TaskParamConfig> taskParamConfigs) {
        this.taskParamConfigs = taskParamConfigs;
    }

    public String getTaskCode()
    {
        return taskCode;
    }

    public void setTaskCode(String taskCode)
    {
        this.taskCode = taskCode;
    }

    public String getTaskDescription()
    {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription)
    {
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
}
