package org.snail.rocket.trigger.database;

import com.google.code.or.binlog.BinlogEventV4;

/**
 * binlog消息实体
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-03 10:41
 */

public class BinlogMessage {
    private BinlogEventV4 event;
    private Class<?> eventType;

    public BinlogMessage() {
    }

    public BinlogMessage(BinlogEventV4 event, Class<?> eventType) {
        this.event = event;
        this.eventType = eventType;
    }

    public BinlogEventV4 getEvent() {
        return event;
    }

    public void setEvent(BinlogEventV4 event) {
        this.event = event;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
