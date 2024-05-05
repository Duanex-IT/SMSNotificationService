package com.bitbank.smsnotification.service;

import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;

public interface IncomingSmsService {

    void processSms(IncomingSmsMessage sms);

}
