package com.bitbank.smsnotification.output;

import javax.mail.MessagingException;

public interface IninMailSender {
    void sendMail(String fromPhone, String smsText) throws MessagingException;
}
