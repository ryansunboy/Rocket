package org.snail.rocket.common.utils;

import org.snail.rocket.common.model.Message;
import org.snail.rocket.common.model.PushTaskMessage;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-04-02 16:39
 */

public class ConvertUtils {
    public static Message convertToMessage(PushTaskMessage pushTaskMessage){
        Message message = new Message();
        message.setDelay(pushTaskMessage.getDelay());
        message.setParams(pushTaskMessage.getParams());
        message.setTaskCode(pushTaskMessage.getTaskCode());
        message.setTaskMessage(pushTaskMessage.getTaskMessage());
        message.setTaskName(pushTaskMessage.getTaskName());
        message.setInstanceId(pushTaskMessage.getInstanceId());
        return message;
    }
}
