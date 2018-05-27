package org.snail.rocket.support;

import java.util.List;

public class FilterConditionConfig
{
    private String refTaskCode;
    private List<ConditionConfig> conditionConfigs;

    public String getRefTaskCode() {
        return refTaskCode;
    }

    public void setRefTaskCode(String refTaskCode) {
        this.refTaskCode = refTaskCode;
    }

    public List<ConditionConfig> getConditionConfigs() {
        return conditionConfigs;
    }

    public void setConditionConfigs(List<ConditionConfig> conditionConfigs) {
        this.conditionConfigs = conditionConfigs;
    }

    public FilterConditionConfig(String refTaskCode, List<ConditionConfig> conditionConfigs) {
        this.refTaskCode = refTaskCode;
        this.conditionConfigs = conditionConfigs;
    }
}
