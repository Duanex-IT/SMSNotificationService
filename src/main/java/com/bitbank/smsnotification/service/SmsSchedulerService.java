package com.bitbank.smsnotification.service;

import com.bitbank.smsnotification.domain.message.*;
import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SmsSchedulerService {

    SmsResponse addSmsMessageToQueue(SmsMessage sms) throws HibernateException;

    SmsResponse addSmsMessageToQueue(SmsMessage sms, boolean sendImmediately) throws HibernateException;

    void addSmsMessagesToQueue(List<SmsMessage> smsMessages) throws HibernateException;

    void addSmsMessagesToQueue(List<SmsMessage> smsMessages, boolean sendImmediately) throws HibernateException;

}

