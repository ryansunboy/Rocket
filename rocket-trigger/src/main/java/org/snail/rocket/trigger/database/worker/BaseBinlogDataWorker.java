package org.snail.rocket.trigger.database.worker;

import org.snail.rocket.common.utils.RocketNamedThreadFactroy;
import org.snail.rocket.dispatcher.Dispatcher;
import org.snail.rocket.trigger.database.BinlogMessage;
import org.snail.rocket.trigger.database.handletask.BinlogDataHandleTask;
import org.snail.rocket.trigger.support.BinlogMessageQueue;
import org.snail.rocket.trigger.support.MonitorDataKeeper;
import org.snail.rocket.trigger.support.TableInfoKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通用的binlog数据处理worker
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-02 19:08
 */

public abstract class BaseBinlogDataWorker {
    private static Logger logger = LoggerFactory.getLogger(BaseBinlogDataWorker.class);
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final String DEFAULT_WORKER_NAME = "BinlogDataWorker";
    protected int workerThreadPoolSize = 5;
    protected ThreadPoolExecutor workerThreadPool;
    protected String workerName;
    protected BinlogMessageQueue binlogMessageQueue;
    protected Dispatcher dispatcher;
    protected MonitorDataKeeper monitorDataKeeper;
    protected TableInfoKeeper tableInfoKeeper;

    public BaseBinlogDataWorker() {
    }

    public BaseBinlogDataWorker(BinlogMessageQueue binlogMessageQueue, Dispatcher dispatcher,MonitorDataKeeper monitorDataKeeper,TableInfoKeeper tableInfoKeeper) {
        this(DEFAULT_POOL_SIZE,DEFAULT_WORKER_NAME,binlogMessageQueue,dispatcher,monitorDataKeeper,tableInfoKeeper);
    }

    /**
     * @Title  BaseBinlogDataWorker.java
     * @Package net.hs.rocket.trigger.database.worker
     * @Description 初始化处理线程池
     * @author shouchen21647@hundsun.com
     * @date 2018-01-09 13:28
     * @params
     * @param workerThreadPoolSize
     * @param workerName
     * @param binlogMessageQueue
     * @param dispatcher
     * @return
     */
    public BaseBinlogDataWorker(int workerThreadPoolSize, final String workerName, BinlogMessageQueue binlogMessageQueue, Dispatcher dispatcher,MonitorDataKeeper monitorDataKeeper,TableInfoKeeper tableInfoKeeper) {
        this.workerThreadPoolSize = workerThreadPoolSize;
        this.workerName = workerName;
        //跟Executors.newFixedThreadPool的区别在于使用有界的LinkedBlockingQueue
        workerThreadPool = new ThreadPoolExecutor(workerThreadPoolSize, workerThreadPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(100),new RocketNamedThreadFactroy(workerName,"HandleTaskThread"));
        //当任务队列满时,采用CallerRunsPolicy策略阻塞住生产线程
        workerThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        this.binlogMessageQueue = binlogMessageQueue;
        this.dispatcher = dispatcher;
        this.monitorDataKeeper = monitorDataKeeper;
        this.tableInfoKeeper = tableInfoKeeper;
    }

    /**
     * @Title  BaseBinlogDataWorker.java
     * @Package net.hs.rocket.trigger.database.worker
     * @Description 开启binlog数据处理工作线程
     * @author shouchen21647@hundsun.com
     * @date 2018-01-09 13:05
     * @param
     * @return
     */
    public void start(){
        logger.info("start BinlogDataWorker!");
        Thread binlogDataWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        BinlogMessage binlogMessage = binlogMessageQueue.popBinlogMessage();
                        workerThreadPool.execute(getBinlogDataHandleTask(binlogMessage,monitorDataKeeper,tableInfoKeeper));
                    } catch (Exception e){
                        logger.error("binlogDataWorker has error!",e);
                    }


                }

            }
        });
        binlogDataWorker.setName(workerName);
        binlogDataWorker.start();
    }

    /**
     * @Title  BaseBinlogDataWorker.java
     * @Package net.hs.rocket.trigger.database.worker
     * @Description 每个子类注入不同的binlogHandleTask实现
     * @author shouchen21647@hundsun.com
     * @date 2018-01-09 13:19
     * @param binlogMessage
     * @return BinlogDataHandleTask
     */
    protected abstract BinlogDataHandleTask getBinlogDataHandleTask(BinlogMessage binlogMessage, MonitorDataKeeper monitorDataKeeper, TableInfoKeeper tableInfoKeeper);
}
