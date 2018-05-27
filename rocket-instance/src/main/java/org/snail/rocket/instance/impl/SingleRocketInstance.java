package org.snail.rocket.instance.impl;

import com.google.code.or.binlog.BinlogEventListener;
import org.snail.rocket.TableModeXmlRocketConfiguration;
import org.snail.rocket.common.constants.RowsEventConstant;
import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.common.utils.DBAnalysisUtils;
import org.snail.rocket.dispatcher.Dispatcher;
import org.snail.rocket.instance.AbstractRocketInstance;
import org.snail.rocket.support.TableConfig;
import org.snail.rocket.support.TaskConfig;
import org.snail.rocket.trigger.database.AutoOpenReplicator;
import org.snail.rocket.trigger.database.BinlogDataListener;
import org.snail.rocket.trigger.database.worker.BaseBinlogDataWorker;
import org.snail.rocket.trigger.database.worker.DefaultBinlogDataWorker;
import org.snail.rocket.TaskModeXmlRocketConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snail.rocket.trigger.support.BinlogMessageQueue;
import org.snail.rocket.trigger.support.MonitorDataKeeper;
import org.snail.rocket.trigger.support.RocketInitUtil;
import org.snail.rocket.trigger.support.TableInfoKeeper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.*;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-25 15:47
 */

public class SingleRocketInstance extends AbstractRocketInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleRocketInstance.class);
    private RocketConfig rocketConfig;
    private Dispatcher dispatcher;
    protected MonitorDataKeeper monitorDataKeeper = new MonitorDataKeeper();
    protected TableInfoKeeper tableInfoKeeper = new TableInfoKeeper();
    private ExecutorService executorService = new ThreadPoolExecutor(10, 10, 30, TimeUnit.MINUTES,
            new ArrayBlockingQueue<Runnable>(20), new ThreadFactory()
    {
        @Override
        public Thread newThread(Runnable r)
        {
            return new Thread(r, "PushTask_thread");
        }
    });
    @Override
    public void start() {
        if (!isStart()) {
            super.start();
            try {
                Long serverId = DBAnalysisUtils.getServerId(rocketConfig.getHost() + ":" + rocketConfig.getPort(),
                        RowsEventConstant.DATABASE_DEFAULT_SCHEMA, rocketConfig.getUsername(),
                        rocketConfig.getPassword());
                rocketConfig.setServerid(serverId);
            } catch (Exception e) {
                throw new RocketException("获取server_id失败！",e);
            }
            InputStream webrocketXml = null;
            List<String> rocketXMLPaths = rocketConfig.getXmlPaths();
            if(CollectionUtils.isEmpty(rocketXMLPaths)){
                throw new RocketException("配置文件为空！");
            }
            for(String rocketXMLPath : rocketXMLPaths) {
                if (!StringUtils.isEmpty(rocketXMLPath)) {
                    LOGGER.info("config the path of rocket-monitor-configxml，use the rocket-monitor-config.xml file in the path {}", rocketXMLPath);
                    try {
                        webrocketXml = new FileInputStream(rocketXMLPath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                if (!StringUtils.isEmpty(rocketConfig.getMode()) && rocketConfig.getMode().equals("taskLevel")) {
                    TaskModeXmlRocketConfiguration xmlrocketConfigurationTaskMode = new TaskModeXmlRocketConfiguration(webrocketXml);
                    List<TaskConfig> taskConfigs = xmlrocketConfigurationTaskMode.getAllConfigs();
                    if (CollectionUtils.isEmpty(taskConfigs)) {
                        throw new RocketException("解析rocke.xml文件异常，请检查文件格式正确性！");
                    }
                    RocketInitUtil.initByTaskConfigs(rocketConfig, taskConfigs, monitorDataKeeper, tableInfoKeeper);

                } else {
                    TableModeXmlRocketConfiguration tableModeXmlRocketConfiguration = new TableModeXmlRocketConfiguration(
                            webrocketXml);
                    List<TableConfig> tableConfigs = tableModeXmlRocketConfiguration.getAllConfigs();
                    if (CollectionUtils.isEmpty(tableConfigs)) {
                        throw new RocketException("解析rocke.xml文件异常，请检查文件格式正确性！");
                    }
                    RocketInitUtil.initByTableConfigs(rocketConfig, tableConfigs, monitorDataKeeper, tableInfoKeeper);
                }
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        AutoOpenReplicator or = new AutoOpenReplicator();
                        BinlogMessageQueue binlogMessageQueue = new BinlogMessageQueue();
                        LOGGER.info("Initialize class AutoOpenReplicator by rocket-db-config.properties file in the classpath");
                        or.setEncoding(rocketConfig.getEncoding());
                        or.setAutoReconnect(rocketConfig.getAutoreconnect());
                        or.setDefaultTimeout(rocketConfig.getTimeout());
                        or.setUser(rocketConfig.getUsername());
                        or.setPassword(rocketConfig.getPassword());
                        or.setHost(rocketConfig.getHost());
                        or.setPort(rocketConfig.getPort());
                        or.setServerId(rocketConfig.getServerid());
                        or.setBinlogEventListener(new BinlogDataListener(or, binlogMessageQueue,tableInfoKeeper));
                        or.setHeartbeatPeriod(rocketConfig.getHeartbeat());
                        BinlogEventListener binlogEventListener = or.getBinlogEventListener();
                        if (binlogEventListener instanceof BinlogDataListener) {
                            BinlogDataListener binlogDataListener = (BinlogDataListener) binlogEventListener;
                            BaseBinlogDataWorker binlogDataWorker = new DefaultBinlogDataWorker(
                                    binlogDataListener.getBinlogMessageQueue(), dispatcher,monitorDataKeeper,tableInfoKeeper);
                            binlogDataWorker.start();
                        }
                        or.start();
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("init rocket error!", e);
                    }
                }
            });
            LOGGER.info(String.format("实例[%s]启动成功！",rocketConfig.getInstanceId()));
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStart() {
        return false;
    }

    public RocketConfig getRocketConfig() {
        return rocketConfig;
    }

    public SingleRocketInstance(RocketConfig rocketConfig,Dispatcher dispatcher) {
        this.rocketConfig = rocketConfig;
        this.dispatcher = dispatcher;
    }

    public void setRocketConfig(RocketConfig rocketConfig) {
        this.rocketConfig = rocketConfig;
    }


}
