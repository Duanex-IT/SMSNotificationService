package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDao;
import com.bitbank.smsnotification.domain.DeliveryReport;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.jsmpp.util.DeliveryReceiptState;

import java.util.Date;
import java.util.List;

public interface DeliveryReportDao extends AbstractDao<DeliveryReport, Long> {

}
