package org.snail.rocket.message;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:22
 */

public class LoginReplyMsg extends BaseMsg{
    private String auth;
    public LoginReplyMsg() {
        super(null);
        setType(MsgType.LOGINREPLY);
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
