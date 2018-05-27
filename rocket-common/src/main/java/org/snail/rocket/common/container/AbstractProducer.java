package org.snail.rocket.common.container;

import org.snail.rocket.common.model.MessageStatus;
import org.snail.rocket.common.model.PushTaskMessage;
import org.snail.rocket.common.utils.support.PushResultFlagContainer;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-28 11:24
 */

public abstract class AbstractProducer implements Producer {
    public abstract Logger getLogger();
    protected ExecutorService service;
    protected QueuePool queuePool;

    public AbstractProducer(ExecutorService service, QueuePool queuePool) {
        this.service = service;
        this.queuePool = queuePool;
    }

    @Override
    public void produce(final PushTaskMessage message) {
        message.setStatus(MessageStatus.DISPATCHERING.getVal());
        service.execute(new Runnable()
        {

            @Override
            public void run()
            {

                queuePool.put(message);
                produceNotify(message.getTaskUUID());
            }
        });
    }

    private void produceNotify(String taskUUID)
    {
        AtomicBoolean produceResult = PushResultFlagContainer.getPushResultMap(taskUUID);
        if (produceResult != null)
        {
            synchronized (produceResult)
            {
                produceResult.set(true);
                produceResult.notify();
                getLogger().info(String.format("任务生产成功通知，任务UUID=[%s]", taskUUID));

            }
        }
        else
        {
            getLogger().error(String.format("任务丢失，任务UUID=[%s]", taskUUID));
        }

    }
}
