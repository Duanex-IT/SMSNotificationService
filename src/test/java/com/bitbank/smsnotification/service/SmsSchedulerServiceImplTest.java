
package com.bitbank.smsnotification.service;

import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.domain.message.SmsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class SmsSchedulerServiceImplTest {

    @Autowired
    private SmsMessageDao smsMessageDao;

    @Autowired
    private SmsSchedulerService notificationService;

    @Value(value = "${bit_config}")
    private String configFolder;

    @Test
	public void testSaveMessageForSender() {
        SmsMessage smsMessage = new SmsMessage("testSaveMessageForSender", null, new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        smsMessage.setSmsText("test text");
        smsMessage.setPhone("1111111111");

        List<SmsMessage> initialMsgs = smsMessageDao.findWaitingForSend();

        try {
            SmsResponse response = notificationService.addSmsMessageToQueue(smsMessage);

            assertNotNull(smsMessage.getMessageId());
            assertNotNull(response);
            assertNull(response.getErrorMessage());
            assertNull(response.getErrorCode());

            List<SmsMessage> msgs = smsMessageDao.findWaitingForSend();
            assertNotNull(msgs);
            assertEquals(msgs.size(), initialMsgs.size() + 1);

            boolean messageAdded = false;
            for (SmsMessage msgFromDB: msgs) {
                if (smsMessage.getMessageId() == msgFromDB.getMessageId()) {
                    messageAdded = true;

                    assertEquals(msgFromDB, smsMessage);
                }
            }
            assertTrue(messageAdded);

        } finally {
            smsMessageDao.delete(smsMessage);
        }
    }

    @Test
    @NotTransactional
    public void testSaveMessageForSenderHiberException() {
        SmsMessage smsMessage = new SmsMessage(null, null, new Date(), null, null);
        smsMessage.setSmsText("test text");
        smsMessage.setPhone("1111111111");

        List<SmsMessage> initialMsgs = smsMessageDao.findWaitingForSend();

        SmsResponse response = notificationService.addSmsMessageToQueue(smsMessage);

        assertNotNull(smsMessage.getMessageId());
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());
        assertNotNull(response.getErrorCode());

        List<SmsMessage> msgs = smsMessageDao.findWaitingForSend();
        assertNotNull(msgs);
        assertEquals(msgs.size(), initialMsgs.size());
    }

}
