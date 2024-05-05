package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDao;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.jsmpp.util.DeliveryReceiptState;

import java.util.Date;
import java.util.List;

public interface SmsMessageDao extends AbstractDao<SmsMessage, Long> {

    List<SmsMessage> findWaitingForSend();

    List<SmsMessage> findWaitingForDeliveryReport();

    List<SmsMessage> findWaitingForSend(int dailyLimit);

    long findSentLastDayCount();

    int updateDeliveryStatus(String messageId, Date deliveryDate, DeliveryReceiptState finalStatus);

    int updateDeliveryStatuses();

    List<SmsMessage> findByCustomerId(String customerId, Date dateReceivedFrom, Date dateReceivedTo);
}
