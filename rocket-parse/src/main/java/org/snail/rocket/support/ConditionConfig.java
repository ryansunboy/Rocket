package org.snail.rocket.support;

public class ConditionConfig
{
    private String columnId;

    private String columnName;

    private String columnNo;

    private String columnValue;

    private String columPattern;


    public String getColumnValue()
    {
        return columnValue;
    }

    public void setColumnValue(String columnValue)
    {
        this.columnValue = columnValue;
    }

    public String getColumnId()
    {
        return columnId;
    }

    public void setColumnId(String columnId)
    {
        this.columnId = columnId;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    public String getColumnNo()
    {
        return columnNo;
    }

    public void setColumnNo(String columnNo)
    {
        this.columnNo = columnNo;
    }

    public String getColumPattern() {
        return columPattern;
    }

    public void setColumPattern(String columPattern) {
        this.columPattern = columPattern;
    }

    public ConditionConfig(String columnId, String columnName, String columnNo, String columnValue, String columPattern)
    {
        super();
        this.columnId = columnId;
        this.columnName = columnName;
        this.columnNo = columnNo;
        this.columnValue = columnValue;
        this.columPattern = columPattern;
    }
    
}
