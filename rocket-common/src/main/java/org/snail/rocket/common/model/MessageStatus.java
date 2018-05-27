package org.snail.rocket.common.model;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-09 14:04
 */

public enum MessageStatus {
    //未处理
    UNHANDLE("-1", "待处理状态"),
    //处理中
    HANDLING("0", "处理中状态"),
    //处理完成
    HANDLED("1","处理完成状态"),
    //待分发
    UNDISPATCHER("2","待分发状态"),
    //分发中
    DISPATCHERING("3","分发中状态"),
    //分发完成
    DISPATCHERED("4","分发完成状态"),
    //无效
    INVALID("8","无效状态"),
    //丢弃
    DISCARD("9","丢弃状态");
    private final String val;

    private final String description;

    MessageStatus(String val, String description) {
        this.val = val;
        this.description = description;
    }

    public String getVal() {
        return val;
    }

    public String getDescription() {
        return description;
    }
}
