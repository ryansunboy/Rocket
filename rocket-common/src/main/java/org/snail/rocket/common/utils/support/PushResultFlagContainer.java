package org.snail.rocket.common.utils.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-02-08 15:20
 */

public class PushResultFlagContainer {
    private static ConcurrentHashMap<String, AtomicBoolean> pushResultMap = new ConcurrentHashMap<String, AtomicBoolean>();

    public static void putPushResultMap(String taskKey, AtomicBoolean result) {
        pushResultMap.put(taskKey, result);
    }

    public static AtomicBoolean getPushResultMap(String taskUUID) {
        return pushResultMap.get(taskUUID);

    }

    public static void removePushResultMap(String taskUUID) {
        pushResultMap.remove(taskUUID);

    }

}
