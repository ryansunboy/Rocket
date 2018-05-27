package org.snail.rocket.trigger.database;

import com.alibaba.fastjson.JSON;
import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.QueryEvent;
import com.google.code.or.binlog.impl.event.TableMapEvent;
import com.google.code.or.binlog.impl.event.XidEvent;
import org.snail.rocket.trigger.support.BinlogMessageQueue;
import org.snail.rocket.trigger.support.TableInfoKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * binlog数据监听器，主要监听数据库的数据变化
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-02 19:08
 */

public class BinlogDataListener implements BinlogEventListener {
    private static Logger logger = LoggerFactory.getLogger(BinlogDataListener.class);
    private String eventDatabase;
    private AutoOpenReplicator openReplicator;
    private BinlogMessageQueue binlogMessageQueue;
    private TableInfoKeeper tableInfoKeeper;

    public BinlogDataListener() {
    }

    public DefaultOpenReplicator getOpenReplicator() {
        return openReplicator;
    }

    public void setOpenReplicator(AutoOpenReplicator openReplicator) {
        this.openReplicator = openReplicator;
    }

    public BinlogDataListener(AutoOpenReplicator openReplicator) {
        this.openReplicator = openReplicator;
    }

    public BinlogDataListener(AutoOpenReplicator openReplicator, BinlogMessageQueue binlogMessageQueue,TableInfoKeeper tableInfoKeeper) {
        this.openReplicator = openReplicator;
        this.binlogMessageQueue = binlogMessageQueue;
        this.tableInfoKeeper = tableInfoKeeper;
    }

    public BinlogMessageQueue getBinlogMessageQueue() {
        return binlogMessageQueue;
    }

    public void setBinlogMessageQueue(BinlogMessageQueue binlogMessageQueue) {
        this.binlogMessageQueue = binlogMessageQueue;
    }

    public TableInfoKeeper getTableInfoKeeper() {
        return tableInfoKeeper;
    }

    public void setTableInfoKeeper(TableInfoKeeper tableInfoKeeper) {
        this.tableInfoKeeper = tableInfoKeeper;
    }

    /**
     * 这里只是实现例子，该方法可以自由处理逻辑
     * @param event
     */
    @Override
    public void onEvents(BinlogEventV4 event) {
        try {
            Class<?> eventType = event.getClass();
            openReplicator.updateLastAliveTime();
            // 事务开始
            if (eventType == QueryEvent.class) {
                QueryEvent actualEvent = (QueryEvent) event;
                this.eventDatabase = actualEvent.getDatabaseName().toString();
                logger.debug("事件数据库名：{}", eventDatabase);
                return;
            }

            // 只监控指定数据库
            if (eventDatabase != null && !"".equals(eventDatabase.trim())) {
                logger.debug("EventType:" + eventType + "event:" + JSON.toJSONString(event));
                if (eventType == TableMapEvent.class) {
                    TableMapEvent actualEvent = (TableMapEvent) event;
                    long tableId = actualEvent.getTableId();
                    String tableName = actualEvent.getTableName().toString();
                    tableInfoKeeper.saveTableIdMap(actualEvent);
                    logger.debug("事件数据表ID：{}， 事件数据库表名称：{}", tableId, tableName);

                } else if (eventType == XidEvent.class) {// 结束事务
                    XidEvent actualEvent = (XidEvent) event;
                    long xId = actualEvent.getXid();
                    logger.debug("结束事件ID：{}", xId);
                } else {
                    BinlogMessage binlogMessage = new BinlogMessage(event,eventType);
                    binlogMessageQueue.pushBinlogMessage(binlogMessage);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
