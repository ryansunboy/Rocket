package org.snail.rocket.message;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:26
 */

public class AskParams implements Serializable {

    private static final long serialVersionUID = -1721227989007434384L;
    private String auth;
    private String requestId;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}