package com.bitbank.smsnotification.service;

public interface SmsProcessorService {

    void sendScheduledSmsMessages();

    boolean smsSendingEnabled();

    void sendConcreteSmsMessage(Long smsId);

    void updateDeliveryStatuses();
}
