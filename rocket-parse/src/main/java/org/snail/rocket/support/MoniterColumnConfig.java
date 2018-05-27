package org.snail.rocket.support;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-04 13:34
 */

public class MoniterColumnConfig {
    private String columnId;

    private String columnName;

    private String columnNo;

    public MoniterColumnConfig() {
    }

    public MoniterColumnConfig(String columnId,String columnName) {
        this.columnName = columnName;
        this.columnNo = columnId;
    }

    public MoniterColumnConfig(String columnId, String columnName, String columnNo) {
        this.columnId = columnId;
        this.columnName = columnName;
        this.columnNo = columnNo;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnNo() {
        return columnNo;
    }

    public void setColumnNo(String columnNo) {
        this.columnNo = columnNo;
    }
}
