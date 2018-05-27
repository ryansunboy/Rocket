package org.snail.rocket.task;

import java.util.Map;

/**
 * 任务接口
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-07-10 10:44
 */

public interface Task<T>  {
    T doTask(Map<String, Object> params);
    String getTaskName();
    String getTaskCode();
    String getTaskDescription();
    void setTaskName(String taskName);
    void setTaskCode(String taskCode);
    void setTaskDescription(String taskDescription);
}
