package com.bitbank.smsnotification.dao;

import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.jsmpp.util.DeliveryReceiptState;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class SmsMessageDaoImplTest {

    public static final String CUST_1 = "cust1";
    public static final String CUST_2 = "cust2";

    @Autowired
    private SmsMessageDao smsMessageDao;

    @Test
    public void testFindWaitingForSend() {
        List<SmsMessage> initialMsgs = smsMessageDao.findWaitingForSend();
        assertNotNull(initialMsgs);
        int initCount = initialMsgs.size();

        SmsMessage sms1 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("some text from testFindWaitingForSend()");
        SmsMessage sms2 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms2.setPhone("+380111111111");
        sms2.setSmsText("some text from testFindWaitingForSend()");
        sms2.setDateSent(new Date());
        SmsMessage sms3 = new SmsMessage("someMethod234", "user1", new Date(), MessagePriority.HIGH, SmsMessageType.CLIENT_NOTIFICATION);
        sms3.setPhone("+380111111111");
        sms3.setSmsText("some text from testFindWaitingForSend()");
        smsMessageDao.save(sms1);
        smsMessageDao.save(sms2);
        smsMessageDao.save(sms3);

        List<SmsMessage> msgs = null;
        try {
            msgs = smsMessageDao.findWaitingForSend();
            assertNotNull(msgs);
            assertEquals(2, msgs.size()-initCount);
            for (int i=0; i<msgs.size()-1; i++) {
                assertTrue(msgs.get(i).getPriority().ordinal() >= msgs.get(i+1).getPriority().ordinal());
            }
        } finally {
            smsMessageDao.delete(sms1);
            smsMessageDao.delete(sms2);
            smsMessageDao.delete(sms3);
        }

        msgs = smsMessageDao.findWaitingForSend();
        assertNotNull(msgs);
        assertEquals(initCount, msgs.size());
    }

    @Ignore//todo implement test
    @Test
    public void testFindWaitingForSendLocking() {
        SmsMessage sms1 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("some text from testFindWaitingForSend()");
        smsMessageDao.save(sms1);

        try {
            List<SmsMessage> smss = smsMessageDao.findWaitingForSend();
            List<SmsMessage> smss2 = smsMessageDao.findWaitingForSend();

            assertEquals(1, smss.size());
            assertEquals(0, smss2.size());
        } finally {
            smsMessageDao.delete(sms1);
        }
    }

    @Test
    public void testFindWaitingForDeliveryReport() {
        List<SmsMessage> initialMsgs = smsMessageDao.findWaitingForDeliveryReport();
        assertNotNull(initialMsgs);
        int initCount = initialMsgs.size();

        SmsMessage sms1 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("some text from testFindWaitingForDeliveryReport()");
        SmsMessage sms2 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms2.setPhone("+380111111111");
        sms2.setSmsText("some text from testFindWaitingForDeliveryReport()");
        sms2.setDateSent(new Date());
        SmsMessage sms3 = new SmsMessage("someMethod234", "user1", new Date(), MessagePriority.HIGH, SmsMessageType.CLIENT_NOTIFICATION);
        sms3.setPhone("+380111111111");
        sms3.setSmsText("some text from testFindWaitingForDeliveryReport()");
        sms3.setDateSent(new Date());
        sms3.setDateDelivered(new Date());
        smsMessageDao.save(sms1);
        smsMessageDao.save(sms2);
        smsMessageDao.save(sms3);

        List<SmsMessage> msgs = null;
        try {
            msgs = smsMessageDao.findWaitingForDeliveryReport();
            assertNotNull(msgs);
            assertEquals(1, msgs.size()-initCount);
        } finally {
            smsMessageDao.delete(sms1);
            smsMessageDao.delete(sms2);
            smsMessageDao.delete(sms3);
        }

        msgs = smsMessageDao.findWaitingForDeliveryReport();
        assertNotNull(msgs);
        assertEquals(initCount, msgs.size());
    }

    @Test
    public void testFindSentLastDayCount() {
        long initCount = smsMessageDao.findSentLastDayCount();

        Calendar cal = Calendar.getInstance();

        //should be sent
        SmsMessage sms1 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("some text from testFindSentLastDayCount()");

        //sent today
        SmsMessage sms2 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms2.setPhone("+380111111111");
        sms2.setSmsText("some text from testFindSentLastDayCount()");
        sms2.setDateSent(cal.getTime());

        //sent more that day ago
        SmsMessage sms3 = new SmsMessage("someMethod234", "user1", new Date(), MessagePriority.HIGH, SmsMessageType.CLIENT_NOTIFICATION);
        sms3.setPhone("+380111111111");
        sms3.setSmsText("some text from testFindSentLastDayCount()");
        cal.add(Calendar.DAY_OF_MONTH, -2);
        sms3.setDateSent(cal.getTime());

        smsMessageDao.save(sms1);
        smsMessageDao.save(sms2);
        smsMessageDao.save(sms3);

        try {
            long count = smsMessageDao.findSentLastDayCount();
            assertEquals(1, count-initCount);
        } finally {
            smsMessageDao.delete(sms1);
            smsMessageDao.delete(sms2);
            smsMessageDao.delete(sms3);
        }

        assertEquals(initCount, smsMessageDao.findSentLastDayCount());
    }

    @Test
    public void testFindWaitingForSendWithLimit() {
        long sentToday = smsMessageDao.findSentLastDayCount();

        Calendar cal = Calendar.getInstance();

        //should be sent
        SmsMessage sms1 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("some text from testFindWaitingForSendWithLimit()");

        //sent today
        SmsMessage sms2 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms2.setPhone("+380111111111");
        sms2.setSmsText("some text from testFindWaitingForSendWithLimit()");
        SmsMessage sms3 = new SmsMessage("someMethod", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms3.setPhone("+380111111111");
        sms3.setSmsText("some text from testFindWaitingForSendWithLimit()");

        //sent more that day ago
        SmsMessage sms4 = new SmsMessage("someMethod234", "user1", new Date(), MessagePriority.HIGH, SmsMessageType.CLIENT_NOTIFICATION);
        sms4.setPhone("+380111111111");
        sms4.setSmsText("some text from testFindWaitingForSendWithLimit()");
        cal.add(Calendar.DAY_OF_MONTH, -2);
        sms4.setDateSent(cal.getTime());

        smsMessageDao.save(sms1);
        smsMessageDao.save(sms2);
        smsMessageDao.save(sms3);
        smsMessageDao.save(sms4);

        List<SmsMessage> msgs = null;
        try {
            msgs = smsMessageDao.findWaitingForSend(1);
            assertNotNull(msgs);
            assertEquals(1, msgs.size());
        } finally {
            smsMessageDao.delete(sms1);
            smsMessageDao.delete(sms2);
            smsMessageDao.delete(sms3);
            smsMessageDao.delete(sms4);
        }
    }

    @Test
    @NotTransactional
    public void testUpdateDeliveryStatus() {
        //todo
        SmsMessage sms1 = new SmsMessage("testUpdateDeliveryStatus", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("some text from testFindWaitingForSendWithLimit()");
        sms1.setRemoteMessageId("remoteId");
        smsMessageDao.save(sms1);

        try {
            Date delivDate = new Date();
            assertEquals(1, smsMessageDao.updateDeliveryStatus(sms1.getRemoteMessageId(), delivDate, DeliveryReceiptState.DELIVRD));
            SmsMessage changedSms = smsMessageDao.findById(sms1.getMessageId());
            assertNotNull(changedSms.getDateDelivered());
            assertEquals(DeliveryReceiptState.DELIVRD, changedSms.getDeliveryState());
            //assertEquals(delivDate, changedSms.getDateDelivered());//todo full date, also chech whole project
        } finally {
            smsMessageDao.delete(sms1);
        }
    }

    @Test
    public void testFindByCustomerId() {
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
            assertEquals(2, smsMessageDao.findByCustomerId(CUST_1, null, null).size());
            assertEquals(1, smsMessageDao.findByCustomerId(CUST_1, dateNow, null).size());
            assertEquals(0, smsMessageDao.findByCustomerId(CUST_1, dateNow, dateYest).size());
            assertEquals(2, smsMessageDao.findByCustomerId(CUST_1, dateYest, dateNow).size());

            assertEquals(1, smsMessageDao.findByCustomerId(CUST_2, null, null).size());
            assertEquals(0, smsMessageDao.findByCustomerId(CUST_2, dateNow, new Date()).size());
        } finally {
            smsMessageDao.delete(sms1);
            smsMessageDao.delete(sms2);
            smsMessageDao.delete(sms3);
            smsMessageDao.delete(sms4);
        }
    }

}
