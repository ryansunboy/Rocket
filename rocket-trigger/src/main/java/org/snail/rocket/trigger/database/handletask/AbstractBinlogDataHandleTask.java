package org.snail.rocket.trigger.database.handletask;

import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.*;
import com.google.code.or.common.glossary.Column;
import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;
import org.snail.rocket.common.constants.CommonConstant;
import org.snail.rocket.common.constants.RowsEventConstant;
import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.model.Condition;
import org.snail.rocket.common.utils.CompareUtils;
import org.snail.rocket.dispatcher.Dispatcher;
import org.snail.rocket.trigger.database.BinlogMessage;
import org.snail.rocket.trigger.exception.RocketTriggerException;
import org.snail.rocket.trigger.support.MonitorDataKeeper;
import org.snail.rocket.trigger.support.RocketTaskInfo;
import org.snail.rocket.trigger.support.TableInfo;
import org.snail.rocket.trigger.support.TableInfoKeeper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * binlog数据处理任务抽象类
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-02 19:08
 */

public abstract class AbstractBinlogDataHandleTask implements BinlogDataHandleTask
{
    private static Logger logger = LoggerFactory.getLogger(AbstractBinlogDataHandleTask.class);
    private final String coding = "utf-8";
    protected Dispatcher dispatcher;
    protected BinlogMessage binlogMessage;
    protected MonitorDataKeeper monitorDataKeeper;
    protected TableInfoKeeper tableInfoKeeper;

    public AbstractBinlogDataHandleTask()
    {
    }

    public AbstractBinlogDataHandleTask(Dispatcher dispatcher, BinlogMessage binlogMessage,MonitorDataKeeper monitorDataKeeper,TableInfoKeeper tableInfoKeeper)
    {
        this.dispatcher = dispatcher;
        this.binlogMessage = binlogMessage;
        this.monitorDataKeeper = monitorDataKeeper;
        this.tableInfoKeeper = tableInfoKeeper;
    }

    public Dispatcher getDispatcher()
    {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    /**
     * @Title AbstractBinlogDataHandleTask.java
     * @Package net.hs.rocket.trigger.database.handletask
     * @Description 数据处理后的数据分发方法，由子类实现
     * @Author shouchen21647@hundsun.com
     * @Date 2018-01-09 13:38
     * @Params
     * @param rocketTaskInfo
     * @param taskParam
     * @return void
     */
    public abstract void dispatcherTask(RocketTaskInfo rocketTaskInfo, Map<String,String> taskParam);

    @Override
    public void run()
    {
        try
        {
            binlogDataHandle(binlogMessage);
        }
        catch (Exception e)
        {
            logger.error("The AbstractBinlogDataHandleTask Thread has error!", e);
        }
    }

    @Override
    public void binlogDataHandle(BinlogMessage binlogMessage)
    {
        if (binlogMessage == null)
        {
            return;
        }
        BinlogEventV4 event = binlogMessage.getEvent();
        Class<?> eventType = binlogMessage.getEventType();
        if (eventType == WriteRowsEvent.class)
        { // 插入事件
            WriteRowsEvent actualEvent = (WriteRowsEvent) event;
            long tableId = actualEvent.getTableId();
            TableInfo tableInfo = tableInfoKeeper.getTableInfo(tableId);
            handleEvent(event, eventType, tableInfo);

            logger.debug("写行事件ID：{}", tableId);

        }
        else if (eventType == WriteRowsEventV2.class)
        { // 插入事件V2
            WriteRowsEventV2 actualEvent = (WriteRowsEventV2) event;
            long tableId = actualEvent.getTableId();
            TableInfo tableInfo = tableInfoKeeper.getTableInfo(tableId);
            handleEvent(event, eventType, tableInfo);

            logger.debug("写行事件V2 ID：{}", tableId);

        }
        else if (eventType == UpdateRowsEvent.class)
        { // 更新事件

            UpdateRowsEvent actualEvent = (UpdateRowsEvent) event;
            long tableId = actualEvent.getTableId();
            TableInfo tableInfo = tableInfoKeeper.getTableInfo(tableId);
            handleEvent(event, eventType, tableInfo);

            logger.debug("更新事件ID：{}", tableId);

        }
        else if (eventType == UpdateRowsEventV2.class)
        { // 更新事件V2

            UpdateRowsEventV2 actualEvent = (UpdateRowsEventV2) event;
            long tableId = actualEvent.getTableId();
            TableInfo tableInfo = tableInfoKeeper.getTableInfo(tableId);
            handleEvent(event, eventType, tableInfo);

            logger.debug("更新事件V2 ID：{}", tableId);

        }
        else if (eventType == DeleteRowsEvent.class)
        {// 删除事件

            DeleteRowsEvent actualEvent = (DeleteRowsEvent) event;
            long tableId = actualEvent.getTableId();

            TableInfo tableInfo = tableInfoKeeper.getTableInfo(tableId);
            handleEvent(event, eventType, tableInfo);

            logger.debug("删除事件ID：{}", tableId);

        }
        else if (eventType == DeleteRowsEventV2.class)
        {// 删除事件V2

            DeleteRowsEventV2 actualEvent = (DeleteRowsEventV2) event;
            long tableId = actualEvent.getTableId();

            TableInfo tableInfo = tableInfoKeeper.getTableInfo(tableId);
            handleEvent(event, eventType, tableInfo);

            logger.info("删除事件V2 ID：{}", tableId);

        }
    }

    private void handleEvent(BinlogEventV4 event, Class<?> eventType, TableInfo tableInfo)
    {
        if (tableInfo == null)
        {
            return;
        }
        String key = tableInfo.getFullName();
        if (monitorDataKeeper.getMonitorDataKeyList().contains(key)||monitorDataKeeper.getMonitorDataKeyList().indexOf(key)>-1) {
            List<String> monitorColmuns = monitorDataKeeper.getMonitorDataColumnList(key);
            if (filterActions(key, eventType)) {
                if (eventType == UpdateRowsEvent.class || eventType == UpdateRowsEventV2.class) {
                    if (CollectionUtils.isEmpty(monitorColmuns) || isChange(monitorColmuns, event, eventType)) {
                        printUpdateRowsColumInfo(event, eventType, tableInfo);
                        doAllTasksDispacher(event, eventType, tableInfo);
                    }
                } else if (eventType == WriteRowsEvent.class || eventType == WriteRowsEventV2.class
                        || eventType == DeleteRowsEvent.class || eventType == DeleteRowsEventV2.class) {
                    printRowsColumInfo(event, eventType, tableInfo);
                    doAllTasksDispacher(event, eventType, tableInfo);
                }
            }
        }
    }

    private void doAllTasksDispacher(BinlogEventV4 event, Class<?> eventType, TableInfo tableInfo) {
        String key = tableInfo.getFullName();
        List<RocketTaskInfo> rocketTaskInfos = monitorDataKeeper.getTaskMap().get(key);
        for(RocketTaskInfo rocketTaskInfo : rocketTaskInfos){
            rocketTaskInfo.setTriggerSource(key);
            boolean paramsFlag = true;
            List<Condition> conditions = rocketTaskInfo.getConditions();
            List<Map<String, String>> taskParams = new ArrayList<>();
            List<org.snail.rocket.common.model.Column> paramsColumns = monitorDataKeeper.getParamColumnMap(key + rocketTaskInfo.getTaskName());
            if(CollectionUtils.isEmpty(paramsColumns)){
                paramsFlag = false;
            } else {
                taskParams = getDatasFromRows(event, eventType, paramsColumns, tableInfo);
            }
            if(CollectionUtils.isEmpty(conditions)){
                if(paramsFlag) {
                    for (Map<String, String> taskParam : taskParams) {
                        dispatcherTask(rocketTaskInfo, taskParam);
                    }
                } else {
                    dispatcherTask(rocketTaskInfo, null);
                }
            } else {
                for(Condition condition : conditions){
                    if (filterConditions(eventType, event, tableInfo, condition.getColumns())) {
                        if(paramsFlag) {
                            for (Map<String, String> taskParam : taskParams) {
                                dispatcherTask(rocketTaskInfo, taskParam);
                            }
                        } else {
                            dispatcherTask(rocketTaskInfo, null);
                        }
                    }
                }
            }
        }
    }

    private boolean filterConditions(Class<?> eventType, BinlogEventV4 event, TableInfo tableInfo, List<org.snail.rocket.common.model.Column> conditions)
    {
        if(CollectionUtils.isEmpty(conditions)){
            return true;
        }
        List<Row> rows = new ArrayList<>();
        if (eventType == WriteRowsEvent.class)
        {
            rows = ((WriteRowsEvent) event).getRows();
        }
        else if (eventType == WriteRowsEventV2.class)
        {
            rows = ((WriteRowsEventV2) event).getRows();
        }
        else if (eventType == DeleteRowsEvent.class)
        {
            rows = ((DeleteRowsEvent) event).getRows();
        }
        else if (eventType == DeleteRowsEventV2.class)
        {
            rows = ((DeleteRowsEventV2) event).getRows();
        }
        else if (eventType == UpdateRowsEvent.class || eventType == UpdateRowsEventV2.class)
        {
            List<Pair<Row>> pairs = new ArrayList<>();
            if (eventType == UpdateRowsEvent.class)
            {
                pairs = ((UpdateRowsEvent) event).getRows();
            }
            else if (eventType == UpdateRowsEventV2.class)
            {
                pairs = ((UpdateRowsEventV2) event).getRows();
            }
            for (Pair<Row> pair : pairs)
            {
                Row after = pair.getAfter();
                List<Column> afterColumns = after.getColumns();
                return chargeCondition(tableInfo, conditions, afterColumns);
            }
        }

        for (Row row : rows)
        {
            List<Column> columns = row.getColumns();
            return chargeCondition(tableInfo, conditions, columns);
        }
        return false;

    }

    private boolean chargeCondition(TableInfo tableInfo, List<org.snail.rocket.common.model.Column> conditions, List<Column> afterColumns) {
        for (org.snail.rocket.common.model.Column column : conditions)
        {
            if (Integer.valueOf(column.getNo()) > afterColumns.size())
            {
                throw new RocketTriggerException("输入的过滤条件列的序号错误！");
            }
            Object value = afterColumns.get(Integer.valueOf(column.getNo())).getValue();
            String columnValue = getStringAfterDecode(value, tableInfo);
            String filterValue = getStringAfterDecode(column.getValue(), tableInfo);
            String conditionPattern = column.getPattern();
            boolean res = true;
            switch(conditionPattern){
                case CommonConstant.EQ:
                    res = CompareUtils.EQ(filterValue,columnValue);
                    break;
                case CommonConstant.NEQ:
                case CommonConstant.NE:
                    res = CompareUtils.NEQ(filterValue,columnValue);
                    break;
                case CommonConstant.GT:
                    res = CompareUtils.GT(filterValue,columnValue);
                    break;
                case CommonConstant.GTE:
                case CommonConstant.GE:
                    res = CompareUtils.GTE(filterValue,columnValue);
                    break;
                case CommonConstant.LT:
                    res = CompareUtils.LT(filterValue,columnValue);
                    break;
                case CommonConstant.LTE:
                case CommonConstant.LE:
                    res = CompareUtils.LTE(filterValue,columnValue);
                    break;
                default:
                    throw new RocketException("未知的符号表达式！");
            }
            if(!res){
                return res;
            }
        }
        return true;
    }

    private boolean filterActions(String key, Class<?> eventType)
    {
        List<String> actions = monitorDataKeeper.getMoniterDataActionList(key);
        if(CollectionUtils.isEmpty(actions)){
            return true;
        }
        if (eventType == WriteRowsEvent.class || eventType == WriteRowsEventV2.class)
        {
            return actions.contains(RowsEventConstant.ROWS_EVENT_INSERT);
        }
        else if (eventType == DeleteRowsEvent.class || eventType == DeleteRowsEventV2.class)
        {
            return actions.contains(RowsEventConstant.ROWS_EVENT_DELETE);

        }
        else if (eventType == UpdateRowsEvent.class || eventType == UpdateRowsEventV2.class)
        {
            return actions.contains(RowsEventConstant.ROWS_EVENT_UPDATE);

        }
        return false;

    }

    private void printRowsColumInfo(BinlogEventV4 event, Class<?> eventType, TableInfo tableInfo)
    {
        List<Row> rows = new ArrayList<>();
        String flag = "insert";
        if (eventType == WriteRowsEvent.class)
        {
            rows = ((WriteRowsEvent) event).getRows();
        }
        else if (eventType == WriteRowsEventV2.class)
        {
            rows = ((WriteRowsEventV2) event).getRows();
        }
        else if (eventType == DeleteRowsEvent.class)
        {
            rows = ((DeleteRowsEvent) event).getRows();
            flag = "delete";
        }
        else if (eventType == DeleteRowsEventV2.class)
        {
            rows = ((DeleteRowsEventV2) event).getRows();
            flag = "delete";
        }
        if (CollectionUtils.isEmpty(rows))
        {
            return;
        }
        for (Row row : rows)
        {
            String columnValue = getData(row, tableInfo);
            if (logger.isDebugEnabled())
            {
                if ("insert".equals(flag))
                {
                    logger.info("insert value:" + columnValue);
                }
                else
                {
                    logger.info("delete value:" + columnValue);
                }
            }
        }
    }

    private boolean isChange(List<String> monitorColmuns, BinlogEventV4 event, Class<?> eventType)
    {
        List<Pair<Row>> rows = new ArrayList<>();
        if (eventType == UpdateRowsEvent.class)
        {
            rows = ((UpdateRowsEvent) event).getRows();
        }
        else if (eventType == UpdateRowsEventV2.class)
        {
            rows = ((UpdateRowsEventV2) event).getRows();
        }
        if (CollectionUtils.isEmpty(rows))
        {
            return false;
        }
        if(CollectionUtils.isEmpty(monitorColmuns)){
            return true;
        }
        for (Pair<Row> row : rows)
        {
            List<Column> beforeColumns = row.getBefore().getColumns();
            List<Column> afterColumns = row.getAfter().getColumns();
            for (String columnNo : monitorColmuns)
            {
                Object beforeValue = beforeColumns.get(Integer.valueOf(columnNo));
                Object afterValue = afterColumns.get(Integer.valueOf(columnNo));
                if (beforeValue == null || afterValue == null)
                {
                    return false;
                }
                if (beforeValue.toString().equals(afterValue.toString()))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void printUpdateRowsColumInfo(BinlogEventV4 event, Class<?> eventType, TableInfo tableInfo)
    {
        List<Pair<Row>> rows = new ArrayList<>();
        if (eventType == UpdateRowsEvent.class)
        {
            rows = ((UpdateRowsEvent) event).getRows();
        }
        else if (eventType == UpdateRowsEventV2.class)
        {
            rows = ((UpdateRowsEventV2) event).getRows();
        }
        if (CollectionUtils.isEmpty(rows))
        {
            return;
        }
        for (Pair<Row> row : rows)
        {
            String columnValueBefore = getData(row.getBefore(), tableInfo);
            String columnValueAfter = getData(row.getAfter(), tableInfo);
            if (logger.isDebugEnabled())
            {
                logger.info("beforeUpdate value:" + columnValueBefore);
                logger.info("afterUpdate value:" + columnValueAfter);
            }
        }
    }

    private String getData(Row before, TableInfo tableInfo)
    {
        List<Column> beforeColumns = before.getColumns();
        String columnValue = "";
        for (Column column : beforeColumns)
        {
            Object value = column.getValue();
            if (value instanceof Byte[] || value instanceof byte[])
            {
                try
                {
                    String str;
                    if (tableInfo.getDeCoding() != null)
                    {
                        str = new String((byte[]) value, tableInfo.getDeCoding());
                    }
                    else
                    {
                        str = new String((byte[]) value, coding);
                    }
                    columnValue += str + "  ";
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                columnValue += value + "  ";
            }
        }
        return columnValue;
    }

    protected List<Map<String, String>> getDatasFromRows(BinlogEventV4 event, Class<?> eventType,
                                                         List<org.snail.rocket.common.model.Column> paramColumns, TableInfo tableInfo)
    {
        List<Map<String, String>> taskMarks = new ArrayList<>();
        List<Row> rows = new ArrayList<>();
        if (eventType == WriteRowsEvent.class)
        {
            rows = ((WriteRowsEvent) event).getRows();
        }
        else if (eventType == WriteRowsEventV2.class)
        {
            rows = ((WriteRowsEventV2) event).getRows();
        }
        else if (eventType == DeleteRowsEvent.class)
        {
            rows = ((DeleteRowsEvent) event).getRows();
        }
        else if (eventType == DeleteRowsEventV2.class)
        {
            rows = ((DeleteRowsEventV2) event).getRows();
        }
        else if (eventType == UpdateRowsEvent.class || eventType == UpdateRowsEventV2.class)
        {
            List<Pair<Row>> pairs = new ArrayList<>();
            if (eventType == UpdateRowsEvent.class)
            {
                pairs = ((UpdateRowsEvent) event).getRows();
            }
            else if (eventType == UpdateRowsEventV2.class)
            {
                pairs = ((UpdateRowsEventV2) event).getRows();
            }
            for (Pair<Row> pair : pairs)
            {
                Row after = pair.getAfter();
                List<Column> afterColumns = after.getColumns();
                Map<String, String> taskMark = new HashMap<>();
                for (org.snail.rocket.common.model.Column column : paramColumns)
                {
                    if (Integer.valueOf(column.getNo()) > afterColumns.size())
                    {
                        throw new RocketTriggerException("输入的任务参数列的序号错误！");
                    }
                    Object value = afterColumns.get(Integer.valueOf(column.getNo())).getValue();
                    String columnValue = getStringAfterDecode(value, tableInfo);
                    taskMark.put(column.getId(), columnValue);
                }
                taskMarks.add(taskMark);
            }
            return taskMarks;
        }
        if (CollectionUtils.isEmpty(rows))
        {
            return taskMarks;
        }
        for (Row row : rows)
        {
            List<Column> columns = row.getColumns();
            Map<String, String> taskMark = new HashMap<>();
            for (org.snail.rocket.common.model.Column column : paramColumns)
            {
                if (Integer.valueOf(column.getNo()) > columns.size())
                {
                    throw new RocketTriggerException("输入的任务分组列的序号错误！");
                }
                Object value = columns.get(Integer.valueOf(column.getNo())).getValue();
                String columnValue = getStringAfterDecode(value, tableInfo);
                taskMark.put(column.getId(), columnValue);
            }
            taskMarks.add(taskMark);
        }
        return taskMarks;
    }

    private String getStringAfterDecode(Object value, TableInfo tableInfo)
    {
        String result = "";
        if(value==null){
            return result;
        }
        if (value instanceof Byte[] || value instanceof byte[])
        {
            try
            {
                if (tableInfo.getDeCoding() != null)
                {
                    result = new String((byte[]) value, tableInfo.getDeCoding());
                }
                else
                {
                    result = new String((byte[]) value, coding);
                }
            }
            catch (UnsupportedEncodingException e)
            {
                logger.error("getStringAfterDecode error!", e);
            }
        }
        else
        {
            result = value.toString();
        }
        return result;
    }

    public MonitorDataKeeper getMonitorDataKeeper() {
        return monitorDataKeeper;
    }

    public void setMonitorDataKeeper(MonitorDataKeeper monitorDataKeeper) {
        this.monitorDataKeeper = monitorDataKeeper;
    }

    public TableInfoKeeper getTableInfoKeeper() {
        return tableInfoKeeper;
    }

    public void setTableInfoKeeper(TableInfoKeeper tableInfoKeeper) {
        this.tableInfoKeeper = tableInfoKeeper;
    }
}
