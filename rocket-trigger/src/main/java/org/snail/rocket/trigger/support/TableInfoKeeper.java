package org.snail.rocket.trigger.support;

import com.google.code.or.binlog.impl.event.TableMapEvent;
import org.snail.rocket.common.utils.DBAnalysisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监控数据库表的key与表结构之间的关系信息
 * key格式：数据库名.表名
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-28 17:36
 */

public class TableInfoKeeper {
    private static final Logger logger = LoggerFactory.getLogger(TableInfoKeeper.class);

    private  Map<Long,TableInfo> tabledIdMap = new ConcurrentHashMap<>();

    private  Map<String,String> tableCodingMap = new ConcurrentHashMap<>();

    public  void saveTableIdMap(TableMapEvent tme){
        long tableId = tme.getTableId();
        tabledIdMap.remove(tableId);

        String key = DBAnalysisUtils.generateTableFullName(tme.getDatabaseName().toString(),tme.getTableName().toString()).get(0);
        TableInfo table = new TableInfo();
        table.setDatabaseName(tme.getDatabaseName().toString());
        table.setTableName(tme.getTableName().toString());
        table.setFullName(key);
        table.setDeCoding(getTableCoding(key));

        tabledIdMap.put(tableId, table);
    }

    public  TableInfo getTableInfo(long tableId){
        return tabledIdMap.get(tableId);
    }

    public  void saveTableCoding(String tableName, String coding){
        tableCodingMap.put(tableName,coding);
    }

    public  String getTableCoding(String tableName){
        return tableCodingMap.get(tableName);
    }

    public Map<Long, TableInfo> getTabledIdMap() {
        return tabledIdMap;
    }

    public void setTabledIdMap(Map<Long, TableInfo> tabledIdMap) {
        this.tabledIdMap = tabledIdMap;
    }

    public Map<String, String> getTableCodingMap() {
        return tableCodingMap;
    }

    public void setTableCodingMap(Map<String, String> tableCodingMap) {
        this.tableCodingMap = tableCodingMap;
    }
}