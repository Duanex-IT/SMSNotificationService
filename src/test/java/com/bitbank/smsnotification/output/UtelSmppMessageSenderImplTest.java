package com.bitbank.smsnotification.output;

import com.bitbank.bitutils.utils.ResourceUtils;
import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.exceptions.MessageSenderException;
import com.bitbank.smsnotification.exceptions.SendMessageException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class UtelSmppMessageSenderImplTest {

    @Autowired
    private UtelSmppMessageSenderImpl utelSender;

    @Resource
    private ResourceUtils resources;
    //todo test in not working, if choosen other sender in config
    @Test
    public void testSendMessage() throws SendMessageException, MessageSenderException {
        SmsMessage sms = new SmsMessage("UtelSmppMessageSenderImplTest.testSendMessage", null, new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms.setPhone("+380000000000");
        sms.setSmsText("test sms message from testSendMessage()");

        assertNotNull(utelSender.sendMessage(sms));
    }

    @Test
    public void testCyrillicSendMessage() throws SendMessageException, MessageSenderException {
        SmsMessage sms = new SmsMessage("UtelSmppMessageSenderImplTest.testSendMessage", null, new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms.setPhone("+380674483962");
        sms.setSmsText(resources.getMessage("test.sms.cyrillic.message")+" testSendMessage()");

        assertNotNull(utelSender.sendMessage(sms));
    }

    //todo test failed sms send with MessageSenderException
    //todo test failed sms send with SendMessageException

    @Test(expected = SendMessageException.class)
    public void testSendMessageLong() throws SendMessageException, MessageSenderException {
        SmsMessage sms = new SmsMessage("UtelSmppMessageSenderImplTest.testSendMessage", null, new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
        sms.setPhone("+380000000000");
        sms.setSmsText("Очень длинное сообщение длиной более 256 октетов. Аж жуть какое длиннющее сообщение. И даже еще длиннее, чем вы думали. Ужас просто!");

        assertNotNull(utelSender.sendMessage(sms));
    }

}
