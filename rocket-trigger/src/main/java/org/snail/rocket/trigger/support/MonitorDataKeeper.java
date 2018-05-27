package org.snail.rocket.trigger.support;

import org.snail.rocket.common.model.Column;
import org.snail.rocket.common.model.Condition;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监控数据库所需要的一些配置信息
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-28 17:36
 * @modify by Ryan at 2018-01-15 增加action和过滤条件集合信息
 */

public class MonitorDataKeeper {
    private static final Logger logger = LoggerFactory.getLogger(MonitorDataKeeper.class);

    //监控数据表列表
    private  List<String> monitorDataKeyList = new ArrayList<>();

    //数据表与任务之间的关系
    private  Map<String,List<RocketTaskInfo>> taskMap = new ConcurrentHashMap<>();

    //监控数据表与监控数据表中监控列的关系
    private  Map<String,List<String>> monitorDataColumnListMap = new ConcurrentHashMap<>();
    //监控数据表过滤条件信息
    private  Map<String,List<Condition>> monitorDataConditonListMap = new ConcurrentHashMap<>();
    //监控需要的CRUD操作
    private  Map<String,List<String>> monitorDataActionListMap = new ConcurrentHashMap<>();

    //特定数据表下某个任务与其任务参数之间的关系
    public static Map<String,List<Column>> paramColumnMap = new ConcurrentHashMap<>();


    public  void saveTableIdMap(String key , RocketTaskInfo rocketTaskInfo){
        List<RocketTaskInfo> tasks = taskMap.get(key);
        if(CollectionUtils.isEmpty(tasks)){
            tasks = new ArrayList<>();
        }
        tasks.add(rocketTaskInfo);
        taskMap.put(key, tasks);
    }


    public  List<RocketTaskInfo> getTableInfo(String key){
        return taskMap.get(key);
    }

    public  List<String> getMonitorDataKeyList(){
        return  monitorDataKeyList;
    }

    public  List<Column> getParamColumnMap(String key){
        return  paramColumnMap.get(key);
    }

    public  List<String> getMonitorDataColumnList(String key){
        return  monitorDataColumnListMap.get(key);
    }

    public  List<String> getMoniterDataActionList(String key){

        return monitorDataActionListMap.get(key);

    }
    public  List<Condition> getMoniterDataConditionList(String key){
        return monitorDataConditonListMap.get(key);

    }

    public void setMonitorDataKeyList(List<String> monitorDataKeyList) {
        this.monitorDataKeyList = monitorDataKeyList;
    }

    public Map<String, List<RocketTaskInfo>> getTaskMap() {
        return taskMap;
    }

    public void setTaskMap(Map<String, List<RocketTaskInfo>> taskMap) {
        this.taskMap = taskMap;
    }

    public Map<String, List<String>> getMonitorDataColumnListMap() {
        return monitorDataColumnListMap;
    }

    public void setMonitorDataColumnListMap(Map<String, List<String>> monitorDataColumnListMap) {
        this.monitorDataColumnListMap = monitorDataColumnListMap;
    }

    public Map<String, List<Condition>> getMonitorDataConditonListMap() {
        return monitorDataConditonListMap;
    }

    public void setMonitorDataConditonListMap(Map<String, List<Condition>> monitorDataConditonListMap) {
        this.monitorDataConditonListMap = monitorDataConditonListMap;
    }

    public Map<String, List<String>> getMonitorDataActionListMap() {
        return monitorDataActionListMap;
    }

    public void setMonitorDataActionListMap(Map<String, List<String>> monitorDataActionListMap) {
        this.monitorDataActionListMap = monitorDataActionListMap;
    }

    public static Map<String, List<Column>> getParamColumnMap() {
        return paramColumnMap;
    }

    public static void setParamColumnMap(Map<String, List<Column>> paramColumnMap) {
        MonitorDataKeeper.paramColumnMap = paramColumnMap;
    }
}