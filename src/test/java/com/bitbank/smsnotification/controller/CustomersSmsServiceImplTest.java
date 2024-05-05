package com.bitbank.smsnotification.controller;

import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class CustomersSmsServiceImplTest {

    public static final String CUST_1 = "cust1";
    public static final String CUST_2 = "cust2";

    @Autowired
    private CustomersSmsServiceImpl customersSmsService;

    @Autowired
    private SmsMessageDao smsMessageDao;

    @Test
    public void testGetSmsByCustomerId() {
        Date dateNow = new Date();
        Date dateYest = new Date(dateNow.getTime()-1000*60*60*24);

        SmsMessage sms1 = new SmsMessage("someMethod", "user1", dateNow, MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("some text from testFindWaitingForSendWithLimit()");
        sms1.setCustomerId(CUST_1);
        SmsMessage sms2 = new SmsMessage("someMethod", "user1", dateYest, MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms2.setPhone("+380111111111");
        sms2.setSmsText("some text from testFindWaitingForSendWithLimit()");
        sms2.setCustomerId(CUST_1);

        SmsMessage sms3 = new SmsMessage("someMethod", "user1", dateYest, MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms3.setPhone("+380111111111");
        sms3.setSmsText("some text from testFindWaitingForSendWithLimit()");
        sms3.setCustomerId(CUST_2);

        SmsMessage sms4 = new SmsMessage("someMethod234", "user1", new Date(), MessagePriority.HIGH, SmsMessageType.CLIENT_NOTIFICATION);
        sms4.setPhone("+380111111111");
        sms4.setSmsText("some text from testFindWaitingForSendWithLimit()");

        smsMessageDao.save(sms1);
        smsMessageDao.save(sms2);
        smsMessageDao.save(sms3);
        smsMessageDao.save(sms4);

        try {
            assertEquals(2, customersSmsService.getSmsByCustomerId(CUST_1, null, null).size());
            assertEquals(1, customersSmsService.getSmsByCustomerId(CUST_1, dateNow, null).size());
            assertEquals(0, customersSmsService.getSmsByCustomerId(CUST_1, dateNow, dateYest).size());
            assertEquals(2, customersSmsService.getSmsByCustomerId(CUST_1, dateYest, dateNow).size());

            assertEquals(1, customersSmsService.getSmsByCustomerId(CUST_2, null, null).size());
            assertEquals(0, customersSmsService.getSmsByCustomerId(CUST_2, dateNow, new Date()).size());
        } finally {
            smsMessageDao.delete(sms1);
            smsMessageDao.delete(sms2);
            smsMessageDao.delete(sms3);
            smsMessageDao.delete(sms4);
        }

    }

}
