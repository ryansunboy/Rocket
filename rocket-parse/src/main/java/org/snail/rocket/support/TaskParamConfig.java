package org.snail.rocket.support;

/**
 * 任务参数配置信息
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-04 13:37
 * @modify Ryan 增加任务参数名称定义
 */


public class TaskParamConfig {
    private String taskParamId;

    private String taskParamName;

    private String taskParamColumnNo;
    
    private String taskParamColumnName;


    public TaskParamConfig() {
    }

    public TaskParamConfig(String taskParamId,String taskParamName,String taskParamColumnNo,String taskParamColumnName) {
        this.taskParamName = taskParamName;
        this.taskParamId = taskParamId;
        this.taskParamColumnName=taskParamColumnName;
        this.taskParamColumnNo=taskParamColumnNo;

    }

    public TaskParamConfig(String taskParamId,String taskParamName) {
        this.taskParamName = taskParamName;
        this.taskParamId = taskParamId;

    }

    public String getTaskParamId() {
        return taskParamId;
    }

    public void setTaskParamId(String taskParamId) {
        this.taskParamId = taskParamId;
    }

    public String getTaskParamName() {
        return taskParamName;
    }

    public void setTaskParamName(String taskParamName) {
        this.taskParamName = taskParamName;
    }

    public String getTaskParamColumnNo() {
        return taskParamColumnNo;
    }

    public void setTaskParamColumnNo(String taskParamColumnNo) {
        this.taskParamColumnNo = taskParamColumnNo;
    }

    public String getTaskParamColumnName()
    {
        return taskParamColumnName;
    }

    public void setTaskParamColumnName(String taskParamColumnName)
    {
        this.taskParamColumnName = taskParamColumnName;
    }
    
}
