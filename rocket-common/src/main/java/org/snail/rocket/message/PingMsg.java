package org.snail.rocket.message;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:23
 */

public class PingMsg  extends BaseMsg {
    public PingMsg(String clientId) {
        super(clientId);
        setType(MsgType.PING);
    }
}