package org.snail.rocket.common.exception;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-03 17:06
 */

public class RocketException extends RuntimeException {
    public RocketException() {
    }

    public RocketException(String message) {
        super(message);
    }

    public RocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public RocketException(Throwable cause) {
        super(cause);
    }

    public RocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
