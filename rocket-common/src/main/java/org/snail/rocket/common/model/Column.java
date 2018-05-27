package org.snail.rocket.common.model;

/**
 * 监控列实体
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-04 16:32
 */

public class Column {
    private String id;

    private String no;

    private String name;

    private String value;

    private String pattern;

    public Column() {
    }
    public Column(String id,String no, String name) {
        this.id = id;
        this.no = no;
        this.name = name;
    }

    public Column(String id,String no, String name,String pattern) {
        this(id,no,name);
        this.pattern = pattern;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
