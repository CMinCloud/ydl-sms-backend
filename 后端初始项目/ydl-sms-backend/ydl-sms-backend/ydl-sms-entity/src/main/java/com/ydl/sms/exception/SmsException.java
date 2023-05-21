package com.ydl.sms.exception;


/**
 * 自定义异常
 */
public class SmsException extends RuntimeException {

    public SmsException(String message) {
        super(message);
    }
}
