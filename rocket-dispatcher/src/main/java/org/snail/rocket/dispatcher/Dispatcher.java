package org.snail.rocket.dispatcher;

import org.snail.rocket.common.model.PushTaskMessage;

/**
 * rocket 转发器接口
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2017-12-29 13:17
 */

public interface Dispatcher {

    void dispatcherTask(PushTaskMessage pushTask);

    void dispatcherTask(String message);
}
