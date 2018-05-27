package org.snail.rocket.message;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:26
 */

public class ReplyMsg extends BaseMsg {
    public ReplyMsg(String clientId,String requestId) {
        super(clientId,requestId);
        setType(MsgType.REPLY);
    }
    private ReplyBody body;

    public ReplyBody getBody() {
        return body;
    }

    public void setBody(ReplyBody body) {
        this.body = body;
    }
}