package org.snail.rocket.common.utils;

import org.snail.rocket.common.constants.RowsEventConstant;
import org.snail.rocket.common.exception.RocketException;
import org.snail.rocket.common.model.RocketConfig;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-25 16:22
 */

public class ConfigUtils {
    public static RocketConfig getRocketConfigFromProp(Properties properties,String connectionProperties) {
        RocketConfig rocketConfig = new RocketConfig();
        if(!StringUtils.isEmpty(connectionProperties)){
            String[] entries = connectionProperties.split(";");
            for (int i = 0; i < entries.length; i++) {
                String entry = entries[i];
                if (entry.length() > 0) {
                    int index = entry.indexOf('=');
                    if (index > 0) {
                        String name = entry.substring(0, index);
                        String value = entry.substring(index + 1);
                        properties.setProperty(name, value);
                    } else {
                        // no value is empty string which is how java.util.Properties works
                        properties.setProperty(entry, null);
                    }
                }
            }
        }
        if(properties.get("config.decrypt") == null){
            rocketConfig.setDecrypt(false);
        } else {
            rocketConfig.setDecrypt(getBoolean(properties.get("config.decrypt"),"config.decrypt"));
        }
        rocketConfig.setEncoding(getString(properties.get("rocket.db.encoding"),"rocket.db.encoding"));
        rocketConfig.setEncryptKey(getStringWithNull(properties.get("rocket.encrypt.key")));
        rocketConfig.setAutoreconnect(getBoolean(properties.get("rocket.autoreconnect"),"rocket.autoreconnect"));
        rocketConfig.setTimeout(getInt(properties.get("rocket.timeout"),"rocket.timeout"));
        rocketConfig.setUsername(getString(properties.get("rocket.db.username"),"rocket.db.username"));
        String encryptPwd = getString(properties.get("rocket.db.password"),"rocket.db.password");
        String decryptPwd;
        if(rocketConfig.getDecrypt()) {
            if (StringUtils.isEmpty(rocketConfig.getEncryptKey())) {
                decryptPwd = DESUtils.decrypt(encryptPwd, DESUtils.DEFAULT_DES_KEY);

            } else {
                String key = DESUtils.decrypt(rocketConfig.getEncryptKey(), DESUtils.DEFAULT_DES_KEY);
                decryptPwd = DESUtils.decrypt(encryptPwd, key);
            }
        } else {
            decryptPwd = encryptPwd;
        }
        rocketConfig.setPassword(decryptPwd);
        rocketConfig.setHost(getString(properties.get("rocket.db.host"),"rocket.db.host"));
        rocketConfig.setPort(getInt(properties.get("rocket.db.port"),"rocket.db.port"));
        try {
            Long serverId = DBAnalysisUtils.getServerId(rocketConfig.getHost() + ":" + rocketConfig.getPort(),
                    RowsEventConstant.DATABASE_DEFAULT_SCHEMA, rocketConfig.getUsername(),
                    rocketConfig.getPassword());
            rocketConfig.setServerid(serverId);
        } catch (Exception e) {
            throw new RocketException("获取server_id失败！",e);
        }
        rocketConfig.setHeartbeat(Integer.valueOf(getString(properties.get("rocket.heartbeat"),"rocket.heartbeat")));
        rocketConfig.setInstanceId(UUID.randomUUID().toString());
        return rocketConfig;
    }

    public static List<RocketConfig> getRocketInstancesConfig(String yamlPath,String connectionProperties) throws FileNotFoundException {
        List<RocketConfig> rocketConfigs = new ArrayList<>();
        Yaml yaml = new Yaml();
        Map map = (Map) yaml.load(new FileInputStream(yamlPath));
        Map rocketMap = (Map) map.get("rocket");
        List<Map> instancesList = (List<Map>) rocketMap.get("datasource");
        for(Map instanceMap : instancesList){
            RocketConfig rocketConfig = new RocketConfig();
            Properties properties = new Properties();
            if(!StringUtils.isEmpty(connectionProperties)){
                String[] entries = connectionProperties.split(";");
                for (int i = 0; i < entries.length; i++) {
                    String entry = entries[i];
                    if (entry.length() > 0) {
                        int index = entry.indexOf('=');
                        if (index > 0) {
                            String name = entry.substring(0, index);
                            String value = entry.substring(index + 1);
                            properties.setProperty(name, value);
                        } else {
                            // no value is empty string which is how java.util.Properties works
                            properties.setProperty(entry, null);
                        }
                    }
                }
            }
            if(properties.get("config.decrypt") == null){
                rocketConfig.setDecrypt(false);
            } else {
                rocketConfig.setDecrypt(getBoolean(properties.get("config.decrypt"),"config.decrypt"));
            }
            Map confMap = (Map) instanceMap.get("conf");
            rocketConfig.setAutoreconnect(getBoolean(confMap.get("autoreconnect"),"autoreconnect"));
            rocketConfig.setEncoding(getString(confMap.get("encoding"),"encoding"));
            rocketConfig.setEncryptKey(getStringWithNull(confMap.get("encryptkey")));
            rocketConfig.setUsername(getString(confMap.get("username"),"username"));
            rocketConfig.setHost(getString(confMap.get("host"),"host"));
            rocketConfig.setHeartbeat(getInt(confMap.get("heartbeat"),"heartbeat"));
            rocketConfig.setPort(getInt(confMap.get("port"),"port"));
            rocketConfig.setTimeout(getInt(confMap.get("timeout"),"timeout"));
            rocketConfig.setXmlPaths((List<String>) instanceMap.get("monitorConfig"));
            rocketConfig.setInstanceId(instanceMap.get("id") == null?UUID.randomUUID().toString(): instanceMap.get("id").toString());
            String encryptPwd = getString(confMap.get("password"),"password");
            String decryptPwd;
            if(rocketConfig.getDecrypt()) {
                if (StringUtils.isEmpty(rocketConfig.getEncryptKey())) {
                    decryptPwd = DESUtils.decrypt(encryptPwd, DESUtils.DEFAULT_DES_KEY);

                } else {
                    String key = DESUtils.decrypt(rocketConfig.getEncryptKey(), DESUtils.DEFAULT_DES_KEY);
                    decryptPwd = DESUtils.decrypt(encryptPwd, key);
                }
            } else {
                decryptPwd = encryptPwd;
            }
            rocketConfig.setPassword(decryptPwd);
            rocketConfigs.add(rocketConfig);
        }
        return rocketConfigs;
    }


    private static String getString(Object o,String keyName)
    {
        if (o == null)
        {
            throw new RocketException("获取的配置文件"+keyName+"null!");
        }
        return o.toString();
    }

    private static String getStringWithNull(Object o)
    {
        if (o == null)
        {
            return null;
        }
        return o.toString();
    }

    private static Boolean getBoolean(Object o,String keyName)
    {
        if (o == null)
        {
            throw new RocketException("获取的配置文件"+keyName+"null!");
        }
        return Boolean.valueOf(o.toString());
    }

    private static Integer getInt(Object o,String keyName)
    {
        if (o == null)
        {
            throw new RocketException("获取的配置文件"+keyName+"null!");
        }
        return Integer.valueOf(o.toString());
    }

    public static void main(String[] args) {
        try {
            List<RocketConfig> rocketConfigs = getRocketInstancesConfig("D:\\WorkSpace\\SP6PATCH5\\rocket\\rocket-server\\src\\main\\resources\\rocket-server.yml","config.decrypt=true");
            System.out.println(JSONUtils.toJSONString(rocketConfigs));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
