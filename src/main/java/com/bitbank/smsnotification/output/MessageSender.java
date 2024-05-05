package com.bitbank.smsnotification.output;

import com.bitbank.smsnotification.domain.DeliveryReport;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.exceptions.MessageSenderException;
import com.bitbank.smsnotification.exceptions.SendMessageException;

import java.util.List;

public interface MessageSender {

    String sendMessage(SmsMessage sms) throws MessageSenderException, SendMessageException;

}
