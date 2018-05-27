package org.snail.rocket.trigger.database.handletask;

import org.snail.rocket.trigger.database.BinlogMessage;


/**
 * All rights Reserved, Designed By shouchen21647@hundsun.com
 * @Title  BinlogDataHandleTask.java   
 * @Package net.hs.rocket.trigger.database.handletask   
 * @Description binlog处理任务接口
 * @Author shouchen21647@hundsun.com 
 * @Date 2018-01-09 13:40
 * @Version V1.0 
 * @Copyright: 2018-01-09 shouchen21647@hundsun.com. All rights reserved. 
 */ 
public interface BinlogDataHandleTask extends Runnable{
    void binlogDataHandle(BinlogMessage binlogMessage);
}
