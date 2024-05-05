package com.bitbank.smsnotification.input;

import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.SoapInputMessage;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.domain.message.SmsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class SoapMessageReceiverImplTest {

    public static final String SMS_TEXT = "sms from soap";
    public static final String PHONE = "+380111111111";
    public static final String ACTIVITY_ID = "activityId";
    public static final String CUSTOMER_ID = "customerId";
    @Autowired
    private SmsMessageDao smsMessageDao;

    @Autowired
    private SoapMessageReceiver soapMessageReceiver;

    @Test
    public void testSendSMSMessage() {
        int initialCnt = smsMessageDao.findWaitingForSend().size();

        SoapInputMessage msg = new SoapInputMessage(ACTIVITY_ID, CUSTOMER_ID, PHONE, SMS_TEXT);

        SmsResponse response = soapMessageReceiver.sendSMSMessage(msg);

        SmsMessage persistedSms = null;
        try {
            assertNotNull(response);
            assertNull(response.getErrorCode());
            assertNull(response.getErrorMessage());
            assertNotNull(response.getMessageID());

            persistedSms = smsMessageDao.findById(response.getMessageID());
            assertNotNull(persistedSms);
            assertEquals(persistedSms.getMessageId(), response.getMessageID());
            assertEquals(persistedSms.getPhone(), PHONE);
            assertEquals(persistedSms.getSmsText(), SMS_TEXT);

            assertEquals(ACTIVITY_ID, persistedSms.getSourceData());
            assertEquals(CUSTOMER_ID, persistedSms.getCustomerId());

            assertEquals(initialCnt+1, smsMessageDao.findWaitingForSend().size());
        } finally {
            if (persistedSms != null) {
                smsMessageDao.delete(persistedSms);
            }
        }

    }

}
