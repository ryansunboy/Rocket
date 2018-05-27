package org.snail.rocket.trigger.support;

/**
 * 监控数据库表的基础信息
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-28 17:35
 */

public class TableInfo {
    private String databaseName;
    private String tableName;
    private String fullName;
    private String deCoding;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDeCoding() {
        return deCoding;
    }

    public void setDeCoding(String deCoding) {
        this.deCoding = deCoding;
    }


}
