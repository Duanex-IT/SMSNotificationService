package com.bitbank.smsnotification.exceptions;

/**
 * Exception, that occured in MessageSender and mean the message is broken, but sender is able to continue sending messages
 */
public class SendMessageException extends Exception {
    public SendMessageException() {
    }

    public SendMessageException(String message) {
        super(message);
    }

    public SendMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendMessageException(Throwable cause) {
        super(cause);
    }
}
