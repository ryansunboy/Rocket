package org.snail.rocket.common.utils.support;

/**
 * 数据库列实体
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-28 16:11
 */

public class Column {
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 列名称(字段名称)
     */
    private String name;
    /**
     * 列编号(第几列)
     */
    private String no;
    /**
     * 是否主键
     */
    private  int isPk;
    /**
     * 默认值
     */
    private String value;
    /**
     * 是否为空
     */
    private int isNotNull;
    /**
     * 数据类型
     */
    private String type;
    /**
     * 数据长度
     */
    private int length;
    /**
     * 代码类型
     */
    private int codeType;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPk() {
        return isPk;
    }

    public void setPk(int pk) {
        isPk = pk;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getNotNull() {
        return isNotNull;
    }

    public void setNotNull(int notNull) {
        isNotNull = notNull;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCodeType() {
        return codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "Column{" +
                "tableName='" + tableName + '\'' +
                ", name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", isPk=" + isPk +
                ", value='" + value + '\'' +
                ", isNotNull=" + isNotNull +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", codeType=" + codeType +
                '}';
    }
}
