package org.snail.rocket.common.model;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-03-01 15:06
 */

public class Condition {
    private String refTaskCode;
    private List<Column> columns;

    public Condition(String refTaskCode, List<Column> columns) {
        this.refTaskCode = refTaskCode;
        this.columns = columns;
    }

    public String getRefTaskCode() {
        return refTaskCode;
    }

    public void setRefTaskCode(String refTaskCode) {
        this.refTaskCode = refTaskCode;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
