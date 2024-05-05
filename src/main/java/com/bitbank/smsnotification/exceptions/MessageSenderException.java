package com.bitbank.smsnotification.exceptions;

/**
 * Critical exception, that occured in MessageSender and indicates,
 * that sms sending should stop and notification should be sent to administrator
 */
public class MessageSenderException extends Exception {
    public MessageSenderException() {
    }

    public MessageSenderException(String message) {
        super(message);
    }

    public MessageSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageSenderException(Throwable cause) {
        super(cause);
    }
}
