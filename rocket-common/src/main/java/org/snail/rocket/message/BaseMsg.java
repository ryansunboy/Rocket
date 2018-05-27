package org.snail.rocket.message;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:21
 */

public class BaseMsg implements Serializable{

    private static final long serialVersionUID = 6853692114994797492L;
    private MsgType type;
    //必须唯一，否者会出现channel调用混乱
    private String clientId;
    private String requestId;

    //初始化客户端id
    public BaseMsg(String clientId,String requestId) {
        this.clientId = clientId;
        this.requestId = requestId;
    }

    public BaseMsg(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
