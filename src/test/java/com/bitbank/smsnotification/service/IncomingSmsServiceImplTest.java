package com.bitbank.smsnotification.service;

import com.bitbank.bitutils.utils.ResourceUtils;
import com.bitbank.notificator.core.sender.EmailSender;
import com.bitbank.smsnotification.dao.IncomingSmsMessageDao;
import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class IncomingSmsServiceImplTest {

    @Autowired
    private IncomingSmsMessageDao incomingSmsDao;

    @ReplaceWithMock
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private IncomingSmsService incSmsService;

    @Autowired
    private ResourceUtils resources;

    @Test
    @NotTransactional
    public void testProcessSms() throws MessagingException {
        IncomingSmsMessage sms = new IncomingSmsMessage();
        sms.setSourceChannel("testProcessSms");
        sms.setSmsText("test testProcessSms");
        sms.setFromPhone("+380000000000");
        sms.setDateReceived(new Date());

        incSmsService.processSms(sms);

        try {
            verify(emailSender, times(1)).sendBusinessMail(anyString(), anyString(), anyString(),
                    eq(resources.getMessage("inin.input.sms.mail.body", new Object[] {sms.getFromPhone(), sms.getSmsText()})));
            assertEquals(sms.getMessageId(), incomingSmsDao.findById(sms.getMessageId()).getMessageId());
        } finally {
            incomingSmsDao.delete(sms);
        }
    }

}
