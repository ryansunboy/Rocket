package org.snail.rocket.common.utils.support;

import java.util.List;

/**
 * 数据库表实体
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-28 16:10
 */

public class Table {
    /**
     * 表名称
     */
    private String name;
    /**
     * 存储空间(库名)
     */
    private String space;
    /**
     * 表中的列
     */
    List<Column> columns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }


    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", space='" + space + '\'' +
                ", columns=" + columns +
                '}';
    }
}
