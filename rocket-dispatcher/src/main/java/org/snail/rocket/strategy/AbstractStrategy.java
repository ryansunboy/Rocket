package org.snail.rocket.strategy;

import org.snail.rocket.common.container.Consumer;
import org.snail.rocket.common.container.Producer;
import org.snail.rocket.common.model.MessageStatus;
import org.snail.rocket.common.model.PushTaskMessage;
import org.snail.rocket.common.utils.JSONUtils;
import org.snail.rocket.common.utils.RocketNamedThreadFactroy;
import org.snail.rocket.common.utils.support.PushResultFlagContainer;
import org.slf4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-08 14:34
 */

public abstract class AbstractStrategy implements Strategy{
    protected Producer producer;
    protected Consumer consumer;
    protected int consumerWorkerThreadPoolSize = 10;
    protected int producerWorkerThreadPoolSize = 10;
    protected String consumerWorkerName = "DelayConsumerWorker";
    protected String producerWorkerName = "DelayProducerWorker";

    protected ThreadPoolExecutor consumerWorkerThreadPool;
    protected ThreadPoolExecutor producerWorkerThreadPool;

    public AbstractStrategy() {
        consumerWorkerThreadPool = new ThreadPoolExecutor(consumerWorkerThreadPoolSize, consumerWorkerThreadPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(100),new RocketNamedThreadFactroy(consumerWorkerName,"ConsumerTaskThread"));
        //当任务队列满时,采用CallerRunsPolicy策略阻塞住生产线程
        consumerWorkerThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        producerWorkerThreadPool = new ThreadPoolExecutor(producerWorkerThreadPoolSize, producerWorkerThreadPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(100),new RocketNamedThreadFactroy(producerWorkerName,"ProduceTaskThread"));
        //当任务队列满时,采用CallerRunsPolicy策略阻塞住生产线程
        producerWorkerThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        init();
    }

    @Override
    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    @Override
    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void operate(PushTaskMessage pushTask) {
        AtomicBoolean result = new AtomicBoolean();
        PushResultFlagContainer.putPushResultMap(pushTask.getTaskUUID(), result);
        producer.produce(pushTask);
        try
        {
            synchronized (result) {
                while (true)
                {
                    AtomicBoolean processObject = PushResultFlagContainer.getPushResultMap(pushTask.getTaskUUID());
                    if(processObject==null){
                        getLogger().info(String.format("当前任务丢失，任务名称=[%s],任务信息[%s]", pushTask.getTaskName(), JSONUtils.toJSONString(pushTask)));
                        return ;
                    }
                    if(processObject.get()){
                        if(MessageStatus.DISPATCHERED.getVal().equals(pushTask.getStatus())) {
                            getLogger().info(String.format("当前任务生产成功，任务名称=[%s],任务信息[%s]", pushTask.getTaskName(),JSONUtils.toJSONString(pushTask)));
                        } else if(MessageStatus.DISCARD.getVal().equals(pushTask.getStatus())){
                            getLogger().info(String.format("当前任务被丢弃，任务名称=[%s],任务信息[%s]", pushTask.getTaskName(), JSONUtils.toJSONString(pushTask)));
                        } else {
                            getLogger().info(String.format("操作任务成功，当前消息状态[%s]，任务名称=[%s],任务信息[%s]", pushTask.getStatus(),pushTask.getTaskName(), JSONUtils.toJSONString(pushTask)));
                        }
                        processObject.set(false);
                        PushResultFlagContainer.removePushResultMap(pushTask.getTaskUUID());
                        break;
                    }else{
                       // getLogger().info(String.format("当前任务还没有生产成功，任务名称=[%s],任务UUID=[%s]", pushTask.getTaskName(),pushTask.getTaskUUID()));
                        processObject.wait();

                    }
                }

            }
        }
        catch (Exception e)
        {
            getLogger().error(e.getMessage());
            e.printStackTrace();
            return;

        }
    }

    @Override
    public void operate(String message) {

    }

    public abstract void init();
    public abstract Logger getLogger();
}
