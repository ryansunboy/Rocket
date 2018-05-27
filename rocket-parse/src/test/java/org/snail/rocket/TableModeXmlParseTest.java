package org.snail.rocket;



import com.alibaba.fastjson.JSON;
import org.snail.rocket.support.TableConfig;

import java.io.InputStream;
import java.util.List;


/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-11-28 16:20
 */

public class TableModeXmlParseTest {
    public static void main(String args[]) throws Exception {
        InputStream webExtenderXml = TableModeXmlParseTest.class.getClassLoader().getResourceAsStream("rocket.xml");
        TableModeXmlRocketConfiguration xmlCanalConfiguration = new TableModeXmlRocketConfiguration(webExtenderXml);
        List<TableConfig> tableConfigs = xmlCanalConfiguration.getAllConfigs();
        System.out.println(JSON.toJSON(tableConfigs));
    }
}
