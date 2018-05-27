package org.snail.rocket.trigger.support;

import org.snail.rocket.trigger.database.BinlogMessage;
import org.snail.rocket.trigger.exception.RocketTriggerException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * binlog Message 队列，用于数据多线程处理
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-03 10:51
 */

public class BinlogMessageQueue {
    public  BlockingQueue<BinlogMessage> binlogMessageQueue = new LinkedBlockingQueue<BinlogMessage>();
    public  BinlogMessage popBinlogMessage() throws InterruptedException {
        return binlogMessageQueue.take();
    }

    public void pushBinlogMessage(BinlogMessage binlogMessage){
        try {
            if (binlogMessage!=null && !binlogMessageQueue.offer(binlogMessage)) {
                binlogMessageQueue.poll();
                binlogMessageQueue.offer(binlogMessage);
            }
        } catch (Exception e) {
            throw new RocketTriggerException("push binlog message error!",e);
        }
    }
}
