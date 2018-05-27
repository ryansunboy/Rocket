package org.snail.rocket.common.utils;


import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-31 14:30
 */

public class SpringUtils {
    private static final Logger logger = LoggerFactory.getLogger(SpringUtils.class);
    public static ApplicationContext context;
    private static Map<String,Task> cache = new ConcurrentHashMap<>();
    public static Task getTask(Message message)
    {
        String taskClass = message.getTaskMessage();
        if(cache.get(taskClass) != null){
            return cache.get(taskClass);
        }
        Task task;
        try
        {
            Object object = null;
            String nameInContext = getNameInContext(taskClass);
            try
            {
                object = context.getBean(nameInContext);
            }
            catch (Exception e) {
                logger.warn("not found the bean in springContext by default bean name,the default bean name is {}",
                    nameInContext);
               }
            if (object == null) {
                object = context.getBean(Class.forName(taskClass));
                if (object == null)
                {
                    logger.warn("can not get the bean from springContext by class type,the class type is {}", taskClass);
                    object = Class.forName(taskClass).newInstance();
                }
            }
            if (object instanceof Task) {
                task = (Task) object;
                task.setTaskDescription(message.getTaskDescription());
                task.setTaskCode(message.getTaskCode());
                task.setTaskName(message.getTaskName());

            }
            else
            {
                logger.error("the task type must be the type of Task<T>,task type is {}", taskClass);
                throw new RocketException();
            }
        }
        catch (ClassNotFoundException e)
        {
            logger.error("load class {} fail", taskClass);
            throw new RocketException();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            logger.error("initialize class {} fail", taskClass);
            throw new RocketException();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            logger.error("initialize class {} fail", taskClass);
            throw new RocketException();
        }
        cache.put(taskClass,task);
        return task;
    }

    private static String getNameInContext(String taskClass)
    {
        String[] strs = taskClass.split("\\.");
        String str = strs[strs.length - 1];
        str = str.substring(0, 1).toLowerCase() + str.substring(1);
        return str;
    }
}
