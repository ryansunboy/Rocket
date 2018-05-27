package org.snail.rocket.message;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:22
 */

public class LogOutMsg extends BaseMsg{
    public LogOutMsg(String clientId) {
        super(clientId);
        setType(MsgType.LOGOUT);
    }
}
