package org.snail.rocket.common.container;

import com.alibaba.fastjson.JSON;
import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.utils.JSONUtils;
import org.snail.rocket.common.utils.SpringUtils;
import org.snail.rocket.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-28 11:16
 */

public abstract class AbstractConsumer implements Consumer{
    protected QueuePool queuePool;
    protected ExecutorService service;

    public AbstractConsumer(ExecutorService service,QueuePool queuePool) {
        this.queuePool = queuePool;
        this.service = service;
    }

    @Override
    public void consume()
    {

        service.submit(new Runnable(){

            @Override
            public void run()
            {
                Message message= queuePool.take();
                getLogger().info(String.format("开始处理任务[%s]", JSONUtils.toJSONString(message)));
                Map<String, String> params = message.getParams();
                getLogger().info(String.format("任务参数信息[%s]", JSON.toJSONString(params)));
                if(StringUtils.isNotBlank(message.getTaskMessage())) {
                    Task task = SpringUtils.getTask(message);
                    if(task != null) {
                        task.doTask(params);
                        getLogger().info(String.format("-------------任务处理结束----------"));
                    } else  {
                        getLogger().info(String.format("-------------无效的任务----------"));
                    }
                } else {
                    getLogger().info(String.format("-------------无效的任务----------"));
                }

            }

        });
    }

    public abstract Logger getLogger();

    public QueuePool getQueuePool() {
        return queuePool;
    }

    public void setQueuePool(QueuePool queuePool) {
        this.queuePool = queuePool;
    }
}
