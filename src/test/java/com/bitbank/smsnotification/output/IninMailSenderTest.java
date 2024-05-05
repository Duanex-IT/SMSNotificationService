package com.bitbank.smsnotification.output;

import com.bitbank.notificator.core.sender.EmailSender;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class IninMailSenderTest {

    @ReplaceWithMock
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private IninMailSender ininSender;

    @Autowired
    private PropertiesConfiguration properties;

    @Test
    public void testSendMail() throws MessagingException {
        ininSender.sendMail("+380000000000", "some test from testSendMail");

        verify(emailSender, times(1)).sendBusinessMail(eq(properties.getString("inin.from.email")),
                eq(properties.getString("inin.email")), anyString(), contains("some test from testSendMail"));
    }

}
