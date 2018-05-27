package org.snail.rocket;


import com.google.common.collect.Lists;
import org.snail.rocket.common.constants.DocumentConstant;
import org.snail.rocket.common.utils.XmlUtils;

import org.snail.rocket.support.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于Task模式下的XML解析器
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-30 10:51
 */
public class TaskModeXmlRocketConfiguration implements RocketConfiguration<TaskConfig> {

    private List<TaskConfig> taskConfigList = Lists.newArrayList();

    public TaskModeXmlRocketConfiguration(InputStream webExtenderXml) {
        parse(webExtenderXml);
    }

    private void parse(InputStream webExtenderXml) {
        //
        try {
            Document webExtenderDoc = XmlUtils.parse(webExtenderXml);
            List<Element> taskEles = XmlUtils.getChildElementsByTagName(webExtenderDoc.getDocumentElement(), DocumentConstant.DOCUMENT_ELE_TASK);
                for(Element taskEle : taskEles){
                    TaskConfig taskConfig = new TaskConfig();
                    Element taskName = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_NAME);
                    Element taskType = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_TYPE);
                    Element taskCode = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_CODE);
                    Element taskDescription = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_DESC);
                    Element taskClass = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TASK_CLASS);
                    Element taskTables = XmlUtils.getChildElementByTagName(taskEle,DocumentConstant.DOCUMENT_ELE_TABLES);
                    List<TableConfig> tableConfigs = new ArrayList<>();
                    List<Element> tableEles = XmlUtils.getChildElementsByTagName(taskTables, DocumentConstant.DOCUMENT_ELE_TABLE);
                    for(Element tableEle : tableEles){
                        TableConfig tableConfig = new TableConfig();
                        Element databaseName = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_DATABASE_NAME);
                        Element tableName = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_TABLE_NAME);
                        Element tableCoding = XmlUtils.getChildElementByTagName(tableEle,DocumentConstant.DOCUMENT_ELE_TABLE_CODING);
                        Element columnsEle = XmlUtils.getChildElementByTagName(tableEle,"columns");
                        List<MoniterColumnConfig> columnConfigs = new ArrayList<>();
                        if(columnsEle!=null) {
                            List<Element> columns = XmlUtils.getChildElementsByTagName(columnsEle,"column");
                            for (Element column : columns) {
                                String columnId = column.getAttribute("id");
                                String columnName = column.getAttribute("name");
                                String columnNo = column.getAttribute("column-no");
                                MoniterColumnConfig columnConfig = new MoniterColumnConfig(columnId, columnName,columnNo);
                                columnConfigs.add(columnConfig);
                            }
                        }
                        tableConfig.setDatabaseName(XmlUtils.getTextValue(databaseName));
                        tableConfig.setTableName(XmlUtils.getTextValue(tableName));
                        if(tableCoding != null) {
                            tableConfig.setDeCoding(XmlUtils.getTextValue(tableCoding));
                        }
                        tableConfig.setColumnConfigs(columnConfigs);
                        Element taskParamsEle = XmlUtils.getChildElementByTagName(tableEle,"task-param-columns");
                        List<Element> taskParams = XmlUtils.getChildElementsByTagName(taskParamsEle,"task-param-column");
                        List<TaskParamConfig> taskParamConfigs = Lists.newArrayList();
                        for(Element param:taskParams){
                            String taskParamId = param.getAttribute("id");
                            String taskParamName =XmlUtils.getTextValue(param);
                            String taskParamNo = param.getAttribute("no");
                            TaskParamConfig taskParamConfig = new TaskParamConfig(taskParamId,taskParamName);
                            taskParamConfig.setTaskParamColumnNo(taskParamNo);
                            taskParamConfigs.add(taskParamConfig);
                        }
                        tableConfig.setTaskParamConfigs(taskParamConfigs);
                        tableConfigs.add(tableConfig);
                        }
                    taskConfig.setTableConfigs(tableConfigs);
                    taskConfig.setTaskName(XmlUtils.getTextValue(taskName));
                    taskConfig.setTaskCode(XmlUtils.getTextValue(taskCode));
                    taskConfig.setTaskDescription(XmlUtils.getTextValue(taskDescription));
                    taskConfig.setTaskType(taskType==null?"":taskType.getAttribute(DocumentConstant.DOCUMENT_ATTR_NAME));
                    taskConfig.setTaskDelayTime(taskType==null?"0":taskType.getAttribute(DocumentConstant.DOCUMENT_ATTR_VALUE));
                    taskConfig.setTaskClass(XmlUtils.getTextValue(taskClass));
                    taskConfigList.add(taskConfig);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TaskConfig> getAllConfigs() {
        return taskConfigList;
    }
}
