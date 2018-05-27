package org.snail.rocket.server.spring;

import org.snail.rocket.trigger.database.BinlogDataListener;
import org.snail.rocket.trigger.database.worker.BaseBinlogDataWorker;
import org.snail.rocket.trigger.database.worker.DefaultBinlogDataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单模式下集成spring 的rocket初始化类
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-30 13:50
 */

public class SpringRocketDefaultInitializeBean extends SpringRocketAbstractInitializeBean
{
    private static Logger LOGGER = LoggerFactory.getLogger(SpringRocketDefaultInitializeBean.class);

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected BaseBinlogDataWorker getBinlogDataWorker(BinlogDataListener binlogDataListener) {
        BaseBinlogDataWorker simpleDataWorker = new DefaultBinlogDataWorker(
                binlogDataListener.getBinlogMessageQueue(), getDispatcher(),monitorDataKeeper,tableInfoKeeper);
        return simpleDataWorker;
    }
}
