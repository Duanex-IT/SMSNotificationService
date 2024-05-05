package com.bitbank.smsnotification.output;

import com.bitbank.smsnotification.domain.DeliveryReport;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StubMessageSenderImpl implements MessageSender {
    @Override
    public String sendMessage(SmsMessage sms) {
        return "stubId";
    }

}
