package com.bitbank.smsnotification.output;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class MessageSenderFactoryTest {

    @Autowired
    private MessageSenderFactory messageSenderFactory;

    @Autowired
    private PropertiesConfiguration properties;

    @Test
    public void testGetConcreteMessageSender() {
        MessageSender sender = messageSenderFactory.getConcreteMessageSender();

        assertNotNull(sender);
        assertEquals(sender.getClass().getSimpleName(), properties.getString("messagesender.implementation.class"));
    }

}
