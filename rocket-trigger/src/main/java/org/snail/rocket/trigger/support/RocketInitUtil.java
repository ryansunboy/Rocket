package org.snail.rocket.trigger.support;

import org.snail.rocket.common.constants.CommonConstant;
import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.model.Column;
import org.snail.rocket.common.model.Condition;
import org.snail.rocket.common.model.RocketConfig;
import org.snail.rocket.common.utils.DBAnalysisUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snail.rocket.support.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-03-07 14:32
 */

public class RocketInitUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketInitUtil.class);
    public static void initByTableConfigs(RocketConfig rocketConfig, List<TableConfig> tableConfigs, MonitorDataKeeper monitorDataKeeper, TableInfoKeeper tableInfoKeeper)
    {
        for (TableConfig tableConfig : tableConfigs) {
            List<String> keys = DBAnalysisUtils.generateTableFullName(tableConfig.getDatabaseName(), tableConfig.getTableName());
            for (String key : keys) {
                if (tableConfig.getDeCoding() == null) {
                    tableInfoKeeper.saveTableCoding(key, rocketConfig.getEncoding());
                } else {
                    tableInfoKeeper.saveTableCoding(key, tableConfig.getDeCoding());
                }
                Map<String, String> columnNameNoMap = getColumnNameNoMap(rocketConfig, tableConfig,key.split(CommonConstant.SPLIT_KEY_SEPARATOR_DOT)[0]);
                monitorDataKeeper.getMonitorDataKeyList().add(key);
                LOGGER.info("add key{} to MonitorDataBaseKeyList.", key);
                List<String> columnNo = new ArrayList<>();
                for (MoniterColumnConfig columnConfig : tableConfig.getColumnConfigs()) {
                    columnConfig.setColumnNo(columnNameNoMap.get(columnConfig.getColumnName()));
                    columnNo.add(columnConfig.getColumnNo());
                }
                monitorDataKeeper.getMonitorDataColumnListMap().put(key, columnNo);
                LOGGER.info("init monitorDataColumnListMap successfully.");

                List<String> actions = new ArrayList<>();

                for (MoniterActionConfig actionConfig : tableConfig.getActionConfigs()) {
                    actions.add(actionConfig.getActionValue());
                }
                monitorDataKeeper.getMonitorDataActionListMap().put(key, actions);
                LOGGER.info("init monitorDataActionListMap successfully.");
                List<Condition> conditions = new ArrayList<>();
                Map<String,List<Condition>> taskCodeCondtionsMap = new HashMap<>();
                for (FilterConditionConfig filterConditionConfig : tableConfig.getConditionConfigs()) {
                    List<Column> columns = new ArrayList<>();
                    List<ConditionConfig> conditionConfigs = filterConditionConfig.getConditionConfigs();
                    for(ConditionConfig conditionConfig : conditionConfigs) {
                        Column column = new Column();
                        column.setId(conditionConfig.getColumnId());
                        column.setName(conditionConfig.getColumnName());
                        column.setNo(columnNameNoMap.get(conditionConfig.getColumnName()));
                        column.setValue(conditionConfig.getColumnValue());
                        column.setPattern(conditionConfig.getColumPattern());
                        columns.add(column);
                    }
                    String refTaskcodes = filterConditionConfig.getRefTaskCode();
                    if(refTaskcodes.contains(CommonConstant.SEPARATOR_DOT)){
                        String[] refTaskcode = refTaskcodes.split(CommonConstant.SEPARATOR_DOT);
                        for(String refTask : refTaskcode){
                            Condition condition = new Condition(refTask, columns);
                            conditions.add(condition);
                            addConditionToMap(key,taskCodeCondtionsMap,condition);
                        }

                    } else  {
                        Condition condition = new Condition(refTaskcodes, columns);
                        conditions.add(condition);
                        addConditionToMap(key,taskCodeCondtionsMap,condition);
                    }
                }
                monitorDataKeeper.getMonitorDataConditonListMap().put(key, conditions);
                LOGGER.info("init monitorDataConditonListMap successfully.");


                List<TaskConfig> taskConfigs = tableConfig.getTaskConfigs();
                for (TaskConfig taskConfig : taskConfigs) {
                    RocketTaskInfo rocketTaskInfo = new RocketTaskInfo();
                    rocketTaskInfo.setTaskMessage(taskConfig.getTaskClass());
                    rocketTaskInfo.setTaskName(taskConfig.getTaskName());
                    rocketTaskInfo.setTaskCode(taskConfig.getTaskCode());
                    rocketTaskInfo.setTaskType(taskConfig.getTaskType());
                    rocketTaskInfo.setTaskDelayTime(taskConfig.getTaskDelayTime());
                    rocketTaskInfo.setTaskDescription(taskConfig.getTaskDescription());
                    rocketTaskInfo.setInstanceId(rocketConfig.getInstanceId());
                    rocketTaskInfo.setConditions(genConditions(taskCodeCondtionsMap,key,taskConfig.getTaskCode()));
                    monitorDataKeeper.saveTableIdMap(key, rocketTaskInfo);
                    List<Column> paramColumns = new ArrayList<>();
                    for (TaskParamConfig taskParamConfig : taskConfig.getTaskParamConfigs()) {
                        taskParamConfig.setTaskParamColumnNo(columnNameNoMap.get(taskParamConfig.getTaskParamName()));
                        Column paramColumn = new Column(taskParamConfig.getTaskParamId(), taskParamConfig.getTaskParamColumnNo(),
                                taskParamConfig.getTaskParamName());
                        paramColumns.add(paramColumn);
                    }
                    MonitorDataKeeper.paramColumnMap.put(key + taskConfig.getTaskName(), paramColumns);
                    LOGGER.info("init paramColumnMap successfully." + key + taskConfig.getTaskName());
                }
            }
        }
    }

    private static List<Condition> genConditions(Map<String, List<Condition>> taskCodeCondtionsMap, String key, String taskCode) {
        String conditionKey = key + CommonConstant.KEY_SEPARATOR_DOT + taskCode;
        List<Condition> conditions = taskCodeCondtionsMap.get(conditionKey);
        List<Condition> keyConditions = taskCodeCondtionsMap.get(key);
        if(CollectionUtils.isEmpty(conditions)){
            conditions = keyConditions;
        } else if(CollectionUtils.isNotEmpty(keyConditions)){
            conditions.addAll(keyConditions);
        }
        return conditions;
    }

    private static void addConditionToMap(String key, Map<String, List<Condition>> taskCodeCondtionsMap, Condition condition) {
        String conditionKey;
        if(StringUtils.isNotBlank(condition.getRefTaskCode())){
            conditionKey = key + CommonConstant.KEY_SEPARATOR_DOT + condition.getRefTaskCode();
        } else {
            conditionKey = key;
        }
        List<Condition> conditions = taskCodeCondtionsMap.get(key);
        if(CollectionUtils.isEmpty(conditions)){
            conditions = new ArrayList<>();
        }
        conditions.add(condition);
        taskCodeCondtionsMap.put(conditionKey,conditions);
    }

    public static void initByTaskConfigs(RocketConfig rocketConfig, List<TaskConfig> taskConfigs,MonitorDataKeeper monitorDataKeeper,TableInfoKeeper tableInfoKeeper)
    {
        for (TaskConfig taskConfig : taskConfigs)
        {
            List<TableConfig> tableConfigs = taskConfig.getTableConfigs();
            for (TableConfig tableConfig : tableConfigs) {
                List<String> keys = DBAnalysisUtils.generateTableFullName(tableConfig.getDatabaseName(), tableConfig.getTableName());
                for (String key : keys) {
                    if (tableConfig.getDeCoding() == null) {
                        tableInfoKeeper.saveTableCoding(key, rocketConfig.getEncoding());
                    } else {
                        tableInfoKeeper.saveTableCoding(key, tableConfig.getDeCoding());
                    }
                    Map<String, String> columnNameNoMap = getColumnNameNoMap(rocketConfig, tableConfig,key.split(CommonConstant.KEY_SEPARATOR_DOT)[0]);
                    monitorDataKeeper.getMonitorDataKeyList().add(key);
                    LOGGER.info("add key{} to MonitorDataBaseKeyList.", key);
                    RocketTaskInfo rocketTaskInfo = new RocketTaskInfo();
                    rocketTaskInfo.setTaskMessage(taskConfig.getTaskClass());
                    rocketTaskInfo.setTaskName(taskConfig.getTaskName());
                    rocketTaskInfo.setTaskCode(taskConfig.getTaskCode());
                    rocketTaskInfo.setTaskType(taskConfig.getTaskType());
                    rocketTaskInfo.setTaskDelayTime(taskConfig.getTaskDelayTime());
                    rocketTaskInfo.setTaskDescription(taskConfig.getTaskDescription());
                    rocketTaskInfo.setInstanceId(rocketConfig.getInstanceId());
                    monitorDataKeeper.saveTableIdMap(key, rocketTaskInfo);
                    List<Column> paramColumns = new ArrayList<>();
                    for (TaskParamConfig taskParamConfig : tableConfig.getTaskParamConfigs()) {
                        taskParamConfig.setTaskParamColumnNo(columnNameNoMap.get(taskParamConfig.getTaskParamName()));
                        Column paramColumn = new Column(taskParamConfig.getTaskParamId(), taskParamConfig.getTaskParamColumnNo(),
                                taskParamConfig.getTaskParamName());
                        paramColumns.add(paramColumn);
                    }
                    MonitorDataKeeper.paramColumnMap.put(key + taskConfig.getTaskName(), paramColumns);
                    LOGGER.info("init paramColumnMap successfully.");
                    List<String> columnName = new ArrayList<>();
                    for (MoniterColumnConfig columnConfig : tableConfig.getColumnConfigs()) {
                        columnConfig.setColumnNo(columnNameNoMap.get(columnConfig.getColumnName()));
                        columnName.add(columnConfig.getColumnNo());
                    }
                    monitorDataKeeper.getMonitorDataColumnListMap().put(key, columnName);
                    LOGGER.info("init monitorDataColumnListMap successfully.");
                }
            }
        }
    }
    public static Map<String, String> getColumnNameNoMap(RocketConfig rocketConfig, TableConfig tableConfig,String dataBaeName)
    {
        Map<String, String> columnNameNoMap;
        try
        {
            columnNameNoMap = DBAnalysisUtils.getColumnNameNoMap(
                    rocketConfig.getHost() + ":" + rocketConfig.getPort(),
                    dataBaeName, rocketConfig.getUsername(),
                    rocketConfig.getPassword(), tableConfig.getTableName());
        }
        catch (ClassNotFoundException e)
        {
            throw new RocketException("获取数据库表结构错误", e);
        }
        catch (SQLException e)
        {
            throw new RocketException("获取数据库表结构错误", e);
        }
        return columnNameNoMap;
    }
}
