package org.snail.rocket;


import com.google.common.collect.Lists;
import org.snail.rocket.common.constants.CommonConstant;
import org.snail.rocket.common.constants.DocumentConstant;
import org.snail.rocket.common.utils.XmlUtils;

import org.apache.commons.lang.StringUtils;
import org.snail.rocket.support.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于Table模式下的XML解析器
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-30 10:51
 * @modify by Ryan at 2018-01-15 解析action和过滤条件信息
 */
public class TableModeXmlRocketConfiguration implements RocketConfiguration<TableConfig> {

    private List<TableConfig> tableConfigList = Lists.newArrayList();

    private static final String DEFAULT_PATTERN = CommonConstant.EQ;

    public TableModeXmlRocketConfiguration(InputStream webExtenderXml) {
        parse(webExtenderXml);
    }

    private void parse(InputStream webExtenderXml) {
        //
        try {
            Document webExtenderDoc = XmlUtils.parse(webExtenderXml);
            List<Element> tableEles = XmlUtils.getChildElementsByTagName(webExtenderDoc.getDocumentElement(), DocumentConstant.DOCUMENT_ELE_TABLE);
                for(Element tableEle : tableEles) {
                    TableConfig tableConfig = new TableConfig();
                    Element databaseName = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_DATABASE_NAME);
                    Element tableName = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_TABLE_NAME);
                    Element tableCoding = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_TABLE_CODING);
                    Element moniterColumnsEle = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_MONITOR_COLUMNS);
                    Element moniterActionsEle = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_MONITOR_ACTIONS);
                    Element moniterConditionsEle = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_FILTER_CONDITONS);
                    List<MoniterColumnConfig> columnConfigs = new ArrayList<>();
                    if(moniterColumnsEle!=null) {
                        List<Element> columns = XmlUtils.getChildElementsByTagName(moniterColumnsEle,DocumentConstant.DOCUMENT_ELE_COLUMN);
                        for (Element column : columns) {
                            String columnId = column.getAttribute(DocumentConstant.DOCUMENT_ATTR_ID);
                            String columnName = column.getAttribute(DocumentConstant.DOCUMENT_ATTR_NAME);
                            String columnNo = column.getAttribute(DocumentConstant.DOCUMENT_ATTR_REF_COL_NO);
                            MoniterColumnConfig columnConfig = new MoniterColumnConfig(columnId, columnName,columnNo);
                            columnConfigs.add(columnConfig);
                        }
                    }
                    tableConfig.setColumnConfigs(columnConfigs);
                    List<MoniterActionConfig> actionConfigs = new ArrayList<>();
                    if(moniterActionsEle!=null){
                        List<Element> actions = XmlUtils.getChildElementsByTagName(moniterActionsEle,DocumentConstant.DOCUMENT_ELE_ACTON);
                        for (Element action : actions)
                        {
                            String actionValue=XmlUtils.getTextValue(action);
                            MoniterActionConfig moniterAction = new MoniterActionConfig(actionValue);
                            actionConfigs.add(moniterAction);
                            
                        }
                    }
                    tableConfig.setActionConfigs(actionConfigs);
                    List<FilterConditionConfig> filterConditionConfigs = new ArrayList<>();
                    if(moniterConditionsEle!=null){
                        List<Element> moniterConditionEles = XmlUtils.getChildElementsByTagName(moniterConditionsEle,DocumentConstant.DOCUMENT_ELE_FILTER_CONDITON);
                        for(Element moniterConditionEle : moniterConditionEles) {
                            List<ConditionConfig> conditionConfigs = new ArrayList<>();
                            List<Element> columns = XmlUtils.getChildElementsByTagName(moniterConditionEle, DocumentConstant.DOCUMENT_ELE_COLUMN);
                            for (Element column : columns) {
                                String columnId = column.getAttribute(DocumentConstant.DOCUMENT_ATTR_ID);
                                String columnName = column.getAttribute(DocumentConstant.DOCUMENT_ATTR_NAME);
                                String columnNo = column.getAttribute(DocumentConstant.DOCUMENT_ATTR_COLUMN_NO);
                                String columnPattern = column.getAttribute(DocumentConstant.DOCUMENT_ATTR_COLUMN_PATTERN);
                                if(StringUtils.isBlank(columnPattern)){
                                    columnPattern = DEFAULT_PATTERN;
                                }
                                String columnValue = XmlUtils.getTextValue(column);
                                ConditionConfig conditionConfig = new ConditionConfig(columnId, columnName, columnNo, columnValue,columnPattern);
                                conditionConfigs.add(conditionConfig);
                            }
                            String refTaskCode = moniterConditionEle.getAttribute(DocumentConstant.DOCUMENT_ATTR_REF_TASK_CODE);
                            FilterConditionConfig filterConditionConfig = new FilterConditionConfig(refTaskCode,conditionConfigs);
                            filterConditionConfigs.add(filterConditionConfig);
                        }
                    }
                    tableConfig.setConditionConfigs(filterConditionConfigs);
                    
                    if(databaseName!=null){
                        tableConfig.setDatabaseName(XmlUtils.getTextValue(databaseName));

                    }
                    if(tableName!=null){
                        tableConfig.setTableName(XmlUtils.getTextValue(tableName));
                    }
                    if(tableCoding != null) {
                        tableConfig.setDeCoding(XmlUtils.getTextValue(tableCoding));
                    }
                    Element tableTasks = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_TASKS);
                    List<TaskConfig> taskConfigs = new ArrayList<>();
                    List<Element> taskEles = XmlUtils.getChildElementsByTagName(tableTasks, DocumentConstant.DOCUMENT_ELE_TASK);
                    for(Element taskEle : taskEles){
                        TaskConfig taskConfig = new TaskConfig();
                        Element taskName = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_NAME);
                        Element taskCode = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_CODE);
                        Element taskType = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_TYPE);
                        Element taskDescription = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_DESC);
                        Element taskClass = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_CLASS);
                        Element taskParamsEle = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_PARAMS);
                        List<TaskParamConfig> taskParamConfigs = Lists.newArrayList();
                        if(taskParamsEle!=null) {
                            List<Element> taskParams = XmlUtils.getChildElementsByTagName(taskParamsEle, DocumentConstant.DOCUMENT_ELE_TASK_PARAM);
                            for (Element param : taskParams) {
                                String taskParamId = param.getAttribute(DocumentConstant.DOCUMENT_ATTR_ID);
                                String taskParamName = XmlUtils.getTextValue(param);
                                if (StringUtils.isEmpty(taskParamName)) {
                                    taskParamName = param.getAttribute(DocumentConstant.DOCUMENT_ATTR_NAME);
                                }
                                String taskParamColumnNo = param.getAttribute(DocumentConstant.DOCUMENT_ATTR_REF_COL_NO);
                                String taskParamColumnName = param.getAttribute(DocumentConstant.DOCUMENT_ATTR_REF_COL_NAME);

                                TaskParamConfig taskParamConfig = new TaskParamConfig(taskParamId, taskParamName, taskParamColumnNo, taskParamColumnName);

                                taskParamConfigs.add(taskParamConfig);
                            }
                        }
                        taskConfig.setTaskCode(XmlUtils.getTextValue(taskCode));
                        taskConfig.setTaskType(taskType==null?"":taskType.getAttribute(DocumentConstant.DOCUMENT_ATTR_NAME));
                        taskConfig.setTaskDelayTime(taskType==null?"0":taskType.getAttribute(DocumentConstant.DOCUMENT_ATTR_TIME));
                        taskConfig.setTaskDescription(XmlUtils.getTextValue(taskDescription));
                        taskConfig.setTaskParamConfigs(taskParamConfigs);
                        taskConfig.setTaskName(XmlUtils.getTextValue(taskName));
                        taskConfig.setTaskClass(XmlUtils.getTextValue(taskClass));
                        taskConfigs.add(taskConfig);
                    }
                    tableConfig.setTaskConfigs(taskConfigs);
                    tableConfigList.add(tableConfig);
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TableConfig> getAllConfigs() {
        return tableConfigList;
    }
}
