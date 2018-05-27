package org.snail.rocket.server.spring;

import com.google.code.or.binlog.BinlogEventListener;
import org.snail.rocket.common.constants.CommonConstant;
import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.common.utils.ConfigUtils;
import org.snail.rocket.common.utils.SpringUtils;
import org.snail.rocket.dispatcher.Dispatcher;
import org.snail.rocket.server.exception.RocketServerException;
import org.snail.rocket.trigger.database.AutoOpenReplicator;
import org.snail.rocket.trigger.database.BinlogDataListener;
import org.snail.rocket.trigger.database.worker.BaseBinlogDataWorker;
import org.snail.rocket.trigger.support.BinlogMessageQueue;
import org.snail.rocket.trigger.support.MonitorDataKeeper;
import org.snail.rocket.trigger.support.RocketInitUtil;
import org.snail.rocket.trigger.support.TableInfoKeeper;
import org.snail.rocket.TableModeXmlRocketConfiguration;
import org.snail.rocket.TaskModeXmlRocketConfiguration;
import org.snail.rocket.support.TableConfig;
import org.snail.rocket.support.TaskConfig;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-18 16:43
 */

public abstract class SpringRocketAbstractInitializeBean
        implements ApplicationListener<ContextRefreshedEvent> {
    private String rocketXMLPath;
    protected String rocketPropertiesPath;
    protected Dispatcher dispatcher;
    private String mode;
    protected String connectionProperties;
    protected ExecutorService executorService = new ThreadPoolExecutor(10, 30, 30, TimeUnit.MINUTES,
            new ArrayBlockingQueue<Runnable>(20), new ThreadFactory()
    {
        @Override
        public Thread newThread(Runnable r)
        {
            return new Thread(r, "PushTask_thread");
        }
    });
    protected MonitorDataKeeper monitorDataKeeper = new MonitorDataKeeper();
    protected final TableInfoKeeper tableInfoKeeper = new TableInfoKeeper();

    protected abstract Logger getLogger();

    protected abstract BaseBinlogDataWorker getBinlogDataWorker(BinlogDataListener binlogDataListener);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            final ApplicationContext context = event.getApplicationContext();
            SpringUtils.context = context;

            InputStream webrocketXml = null;
            List<String> monitorFilePaths = new ArrayList<>();
            Properties properties = new Properties();
            final RocketConfig rocketConfig;
            try {
                if (!StringUtils.isEmpty(rocketXMLPath)) {
                    getLogger().info("config the path of rocket-monitor-configxml，use the rocket-monitor-config.xml file in the path {}", rocketXMLPath);
                    monitorFilePaths = Arrays.asList(rocketXMLPath.split(CommonConstant.MONITOR_XML_SEPARATOR_DOT));
                } else {
                    getLogger().warn("Not config the path of rocket-monitor-config.xml，use the rocket-monitor-config.xml file in classpath");
                    webrocketXml = SpringRocketAbstractInitializeBean.class.getClassLoader().getResourceAsStream("rocket-monitor-config.xml");
                }
                if (!StringUtils.isEmpty(rocketPropertiesPath)) {
                    properties.load(new FileInputStream(rocketPropertiesPath));
                } else {
                    properties.load(
                            SpringRocketAbstractInitializeBean.class.getClassLoader().getResourceAsStream("rocket-db-config.properties"));
                }
                rocketConfig = ConfigUtils.getRocketConfigFromProp(properties,connectionProperties);
               if(mode!=null&&mode.equals("taskLevel")){
                List<TaskConfig> taskConfigs = new ArrayList<>();
                TaskModeXmlRocketConfiguration xmlrocketConfigurationTaskMode;
                if(CollectionUtils.isEmpty(monitorFilePaths)){
                    xmlrocketConfigurationTaskMode = new TaskModeXmlRocketConfiguration(webrocketXml);
                    taskConfigs.addAll(xmlrocketConfigurationTaskMode.getAllConfigs());
                } else {
                    for(String monitorFilePath : monitorFilePaths){
                        webrocketXml = new FileInputStream(monitorFilePath);
                        xmlrocketConfigurationTaskMode = new TaskModeXmlRocketConfiguration(webrocketXml);
                        taskConfigs.addAll(xmlrocketConfigurationTaskMode.getAllConfigs());
                    }
                }
                if (CollectionUtils.isEmpty(taskConfigs)) {
                    throw new RocketServerException("解析rocke.xml文件异常，请检查文件格式正确性！");
                }
                RocketInitUtil.initByTaskConfigs(rocketConfig, taskConfigs,monitorDataKeeper,tableInfoKeeper);
               }else{
                   TableModeXmlRocketConfiguration tableModeXmlRocketConfiguration;
                   List<TableConfig> tableConfigs = new ArrayList<>();
                   if(CollectionUtils.isEmpty(monitorFilePaths)){
                       tableModeXmlRocketConfiguration = new TableModeXmlRocketConfiguration(webrocketXml);
                       tableConfigs.addAll(tableModeXmlRocketConfiguration.getAllConfigs());
                   } else {
                       for(String monitorFilePath : monitorFilePaths){
                           webrocketXml = new FileInputStream(monitorFilePath);
                           tableModeXmlRocketConfiguration = new TableModeXmlRocketConfiguration(webrocketXml);
                           tableConfigs.addAll(tableModeXmlRocketConfiguration.getAllConfigs());
                       }
                   }
                if (CollectionUtils.isEmpty(tableConfigs)) {
                    throw new RocketServerException("解析rocke.xml文件异常，请检查文件格式正确性！");
                }
                RocketInitUtil.initByTableConfigs(rocketConfig, tableConfigs,monitorDataKeeper,tableInfoKeeper);
            }
              getLogger().info("parse the rocket-monitor-config.xml file through task mode successfully");
            } catch (IOException e) {
                getLogger().error("read xml file error! please check the rocket-monitor-config.xml!", e);
                throw new RocketServerException("read xml file error! please check the rocket-monitor-config.xml!");
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object object = null;
                        try {
                            object = context.getBean("autoOpenReplicator");
                        } catch (Exception e) {
                            getLogger().warn(
                                    "Not found class AutoOpenReplicator in the springContext by default name autoOpenReplicator");
                        }
                        AutoOpenReplicator or;
                        if (object != null) {
                            if (!(object instanceof AutoOpenReplicator)) {
                                throw new RocketServerException("从spring context中获取rocket binlog监听解析器失败！");
                            }
                            or = (AutoOpenReplicator) object;
                        } else {
                            BinlogMessageQueue binlogMessageQueue = new BinlogMessageQueue();
                            getLogger().info("Initialize class AutoOpenReplicator by rocket-db-config.properties file in the classpath");
                            or = new AutoOpenReplicator();
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
                        }
                        BinlogEventListener binlogEventListener = or.getBinlogEventListener();
                        if (binlogEventListener instanceof BinlogDataListener) {
                            BinlogDataListener binlogDataListener = (BinlogDataListener) binlogEventListener;
                            BaseBinlogDataWorker binlogDataWorker = getBinlogDataWorker(binlogDataListener);
                            binlogDataWorker.start();
                        }
                        or.start();
                    }
                    catch (Exception e)
                    {
                        getLogger().error("init rocket error!", e);
                    }
                }
            });
        }
    }

    public String getRocketXMLPath() {
        return rocketXMLPath;
    }

    public void setRocketXMLPath(String rocketXMLPath) {
        this.rocketXMLPath = rocketXMLPath;
    }

    public String getRocketPropertiesPath() {
        return rocketPropertiesPath;
    }

    public void setRocketPropertiesPath(String rocketPropertiesPath) {
        this.rocketPropertiesPath = rocketPropertiesPath;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public String getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
    }
}
