package com.bitbank.smsnotification.output;

import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StubMessageSenderImplTest {

    StubMessageSenderImpl stub = new StubMessageSenderImpl();

    @Test
    public void testStubSent() {
        assertNotNull(stub.sendMessage(null));
        assertNotNull(stub.sendMessage(new SmsMessage()));
        assertNotNull(stub.sendMessage(new SmsMessage("sdf", "sdf", new Date(), MessagePriority.HIGH, SmsMessageType.CLIENT_NOTIFICATION)));
    }

}
