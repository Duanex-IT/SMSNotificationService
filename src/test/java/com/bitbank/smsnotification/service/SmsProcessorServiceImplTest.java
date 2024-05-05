package com.bitbank.smsnotification.service;

import com.bitbank.notificator.core.sender.EmailSender;
import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class SmsProcessorServiceImplTest {

    @Autowired
    private SmsProcessorService processorService;

    @Autowired
    private SmsMessageDao smsMessageDao;

    @Autowired
    private PropertiesConfiguration properties;

    @ReplaceWithMock
    @Autowired
    private EmailSender emailSender;

    @Value(value = "${bit_config}")
    private String configFolder;

    @Test
    public void testSmsSendingEnabled() throws IOException {
        File disablingFile = new File(configFolder + SmsSchedulerServiceImpl.STOP_FILE_NAME);
        assertNotEquals(disablingFile.exists(), processorService.smsSendingEnabled());

        if (!disablingFile.exists()) {
            //trying to disable sending
            assertTrue(disablingFile.createNewFile());

            try {
                assertFalse(processorService.smsSendingEnabled());
            } finally {
                disablingFile.delete();
            }
        } else {
            //trying to enable sending
            assertTrue(disablingFile.delete());

            try {
                assertTrue(processorService.smsSendingEnabled());
            } finally {
                disablingFile.createNewFile();
            }
        }

    }

    @Test
    public void testSendScheduledSmsMessages() {

    }

    @Test
    @NotTransactional
    public void testSendFailingSms() {
        SmsMessage sms1 = new SmsMessage("testSendFailingSms", "user1", new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms1.setPhone("+380111111111");
        sms1.setSmsText("кириллицакириллица20кириллицакириллица40кириллицакириллица60кириллицакириллица80кириллицакириллиц100кириллицакириллиц120кириллицакириллиц140");
        smsMessageDao.save(sms1);

        try {
            long minSendDuration = properties.getLong("utel.smpp.capacity.message.duration.millis");

            for (int i=1; i<15; i++) {
                processorService.sendScheduledSmsMessages();

                try {
                    Thread.currentThread().sleep(minSendDuration*3);
                } catch (InterruptedException e) {
                }
            }

            verify(emailSender, times(1)).sendMail(anyString(), anyString());
        } finally {
            smsMessageDao.delete(smsMessageDao.findById(sms1.getMessageId()));//need to refind messsage cause version changed
        }
    }
}
