package org.snail.rocket.support;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-30 10:51
 */

public class TableConfig {
    private String databaseName;

    private String tableName;

    private String deCoding;

    private List<MoniterActionConfig> actionConfigs = new ArrayList<>();
    
    private List<FilterConditionConfig> conditionConfigs = new ArrayList<>();
    
    private List<MoniterColumnConfig> columnConfigs = new ArrayList<>();

    private List<TaskParamConfig> taskParamConfigs = Lists.newArrayList();

    private List<TaskConfig> taskConfigs = Lists.newArrayList();


    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDeCoding() {
        return deCoding;
    }

    public void setDeCoding(String deCoding) {
        this.deCoding = deCoding;
    }

    public List<MoniterColumnConfig> getColumnConfigs() {
        return columnConfigs;
    }

    public void setColumnConfigs(List<MoniterColumnConfig> columnConfigs) {
        this.columnConfigs = columnConfigs;
    }

    public List<TaskParamConfig> getTaskParamConfigs() {
        return taskParamConfigs;
    }

    public void setTaskParamConfigs(List<TaskParamConfig> taskParamConfigs) {
        this.taskParamConfigs = taskParamConfigs;
    }

    public List<TaskConfig> getTaskConfigs() {
        return taskConfigs;
    }

    public void setTaskConfigs(List<TaskConfig> taskConfigs) {
        this.taskConfigs = taskConfigs;
    }

    public List<MoniterActionConfig> getActionConfigs()
    {
        return actionConfigs;
    }

    public void setActionConfigs(List<MoniterActionConfig> actionConfigs)
    {
        this.actionConfigs = actionConfigs;
    }

    public List<FilterConditionConfig> getConditionConfigs()
    {
        return conditionConfigs;
    }

    public void setConditionConfigs(List<FilterConditionConfig> conditionConfigs)
    {
        this.conditionConfigs = conditionConfigs;
    }
    
}
