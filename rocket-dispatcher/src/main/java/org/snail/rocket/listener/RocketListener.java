package org.snail.rocket.listener;


import org.snail.rocket.common.container.Consumer;

/**
 * rocket 监听接口，用于客户端监听是否存在待处理的任务
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-29 14:35
 */

public interface RocketListener {
    void notifyHandleTask(Consumer consumer);
}
