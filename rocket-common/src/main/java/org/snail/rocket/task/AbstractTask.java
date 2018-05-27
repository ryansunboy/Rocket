package org.snail.rocket.task;

/**
 * 任务抽象类
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-20 13:22
 */

public abstract class AbstractTask<T> implements Task<T> {
    private String taskDescription;
    private String taskName;
    private String taskCode;

    @Override
    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String getTaskCode()
    {
        return taskCode;
    }

    @Override
    public void setTaskCode(String taskCode)
    {
        this.taskCode=taskCode;
        
    }

}
