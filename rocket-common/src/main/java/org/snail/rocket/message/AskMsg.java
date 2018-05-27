package org.snail.rocket.message;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-06 16:25
 */

public class AskMsg extends BaseMsg {
    public AskMsg(String clientId,String requestId,String groupType) {
        super(clientId,requestId);
        setType(MsgType.ASK);
        this.groupType = groupType;
    }
    private AskParams params;

    private String groupType;

    public AskParams getParams() {
        return params;
    }

    public void setParams(AskParams params) {
        this.params = params;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}