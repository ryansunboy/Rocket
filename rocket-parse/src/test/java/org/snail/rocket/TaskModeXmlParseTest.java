package org.snail.rocket;


import com.alibaba.fastjson.JSON;
import org.snail.rocket.support.TaskConfig;

import java.io.InputStream;
import java.util.List;


/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-28 16:20
 */

public class TaskModeXmlParseTest {
    public static void main(String args[]) throws Exception {
        InputStream webExtenderXml = TaskModeXmlParseTest.class.getClassLoader().getResourceAsStream("rocket-monitor-config.xml");
        TaskModeXmlRocketConfiguration xmlCanalConfiguration = new TaskModeXmlRocketConfiguration(webExtenderXml);
        List<TaskConfig> taskConfigs = xmlCanalConfiguration.getAllConfigs();
        System.out.println(JSON.toJSON(taskConfigs));
    }
}
