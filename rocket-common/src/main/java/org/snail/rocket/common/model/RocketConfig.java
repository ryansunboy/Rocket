package org.snail.rocket.common.model;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-18 15:06
 */

public class RocketConfig {
    private String instanceId;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Long serverid;
    private Boolean autoreconnect;
    private Integer timeout;
    private String encoding;
    private Integer heartbeat;

    private Boolean isDecrypt = true;

    private String encryptKey;

    private List<String> xmlPaths;

    private String mode;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getServerid() {
        return serverid;
    }

    public void setServerid(Long serverid) {
        this.serverid = serverid;
    }

    public Boolean getAutoreconnect() {
        return autoreconnect;
    }

    public void setAutoreconnect(Boolean autoreconnect) {
        this.autoreconnect = autoreconnect;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public Boolean getDecrypt() {
        return isDecrypt;
    }

    public void setDecrypt(Boolean decrypt) {
        isDecrypt = decrypt;
    }

    public List<String> getXmlPaths() {
        return xmlPaths;
    }

    public void setXmlPaths(List<String> xmlPaths) {
        this.xmlPaths = xmlPaths;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
