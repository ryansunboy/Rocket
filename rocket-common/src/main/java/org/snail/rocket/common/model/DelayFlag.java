package org.snail.rocket.common.model;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-03-13 9:55
 */

public class DelayFlag {
    private Long date;
    private AtomicBoolean flag;
    private PushTaskMessage message;

    public DelayFlag() {
        date = System.currentTimeMillis();
        flag = new AtomicBoolean(false);
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public AtomicBoolean getFlag() {
        return flag;
    }

    public void setFlag(AtomicBoolean flag) {
        this.flag = flag;
    }

    public PushTaskMessage getMessage() {
        return message;
    }

    public void setMessage(PushTaskMessage message) {
        this.message = message;
    }
}
