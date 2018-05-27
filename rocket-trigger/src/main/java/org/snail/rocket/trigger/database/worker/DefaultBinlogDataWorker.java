package org.snail.rocket.trigger.database.worker;

import org.snail.rocket.dispatcher.Dispatcher;
import org.snail.rocket.trigger.database.BinlogMessage;
import org.snail.rocket.trigger.database.handletask.BinlogDataHandleTask;
import org.snail.rocket.trigger.database.handletask.DefaultBinlogDataHandleTask;
import org.snail.rocket.trigger.support.BinlogMessageQueue;
import org.snail.rocket.trigger.support.MonitorDataKeeper;
import org.snail.rocket.trigger.support.TableInfoKeeper;

/**
 * 简单模式下的binlog数据处理worker
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-02 19:08
 */

public class DefaultBinlogDataWorker extends BaseBinlogDataWorker {
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final String DEFAULT_WORKER_NAME = "DefaultBinlogDataWorker";

    public DefaultBinlogDataWorker() {
    }

    public DefaultBinlogDataWorker(BinlogMessageQueue binlogMessageQueue, Dispatcher dispatcher, MonitorDataKeeper monitorDataKeeper, TableInfoKeeper tableInfoKeeper) {
        super(DEFAULT_POOL_SIZE,DEFAULT_WORKER_NAME,binlogMessageQueue, dispatcher,monitorDataKeeper,tableInfoKeeper);
    }

    public DefaultBinlogDataWorker(int workerThreadPoolSize, String workerName, BinlogMessageQueue binlogMessageQueue, Dispatcher dispatcher, MonitorDataKeeper monitorDataKeeper, TableInfoKeeper tableInfoKeeper) {
        super(workerThreadPoolSize, workerName, binlogMessageQueue, dispatcher,monitorDataKeeper,tableInfoKeeper);
    }

    @Override
    protected BinlogDataHandleTask getBinlogDataHandleTask(BinlogMessage binlogMessage, MonitorDataKeeper monitorDataKeeper, TableInfoKeeper tableInfoKeeper) {
        return new DefaultBinlogDataHandleTask(dispatcher,binlogMessage,monitorDataKeeper,tableInfoKeeper);
    }
}
