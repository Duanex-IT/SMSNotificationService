package com.bitbank.smsnotification.output;

import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.service.SmsProcessorService;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.util.DefaultDecomposer;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.PDUDecomposer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class UtelMessageReceiverListenerTest {

    @Autowired
    private UtelMessageReceiverListener messageListener;

    @Autowired
    private SmsMessageDao messageDao;

    @Autowired
    private SmsProcessorService smsProcessorService;

    @Ignore //todo
    @Test(expected = ProcessRequestException.class)
    public void testDeliveryReportNothingToUpdate() throws ProcessRequestException {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();

        DeliveryReceipt delReceipt = new DeliveryReceipt("1234", 1, 1, new Date(), new Date(), DeliveryReceiptState.DELIVRD, null, "test deliv receipt");
        deliverSm.setShortMessage(delReceipt.toString().getBytes());

        messageListener.onAcceptDeliverSm(deliverSm);
    }

    @Ignore//todo
    @Test
    @NotTransactional
    public void testDeliveryReport() throws ProcessRequestException {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();

        SmsMessage sms = new SmsMessage("testDeliveryReport", null, new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION, "+380671111111", "some test text");
        sms.setRemoteMessageId("1234");
        messageDao.save(sms);

        try {
            DeliveryReceipt delReceipt = new DeliveryReceipt("1234", 1, 1, new Date(), new Date(), DeliveryReceiptState.DELIVRD, null, "test deliv receipt");
            deliverSm.setShortMessage(delReceipt.toString().getBytes());

            messageListener.onAcceptDeliverSm(deliverSm);
            smsProcessorService.updateDeliveryStatuses();

            SmsMessage deliveredSms = messageDao.findById(sms.getMessageId());
            assertNotNull(deliveredSms.getDateDelivered());
            assertEquals(DeliveryReceiptState.DELIVRD, deliveredSms.getDeliveryState());
        } finally {
            messageDao.delete(sms);
        }
    }

    @Test
    @NotTransactional
    public void testIncomingSms() throws ProcessRequestException {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setDestAddress("1010");
        deliverSm.setShortMessage("deliv msg".getBytes());
        deliverSm.setSourceAddr("+380671234567");

        deliverSm.setShortMessage("test 3mob deliv receipt".getBytes(Charset.forName("UTF-16")));

        messageListener.onAcceptDeliverSm(deliverSm);

        SmsMessage insertedSms = null;
        try {
            List<SmsMessage> smsList = messageDao.findAll();

            for (SmsMessage sms: smsList) {
                if ("test 3mob deliv receipt".equals(sms.getSmsText())) {
                    insertedSms = sms;
                    assertNotNull(sms.getDateReceived());
                    assertEquals(DeliveryReceiptState.ACCEPTD, sms.getDeliveryState());
                    assertEquals("test 3mob deliv receipt", sms.getSmsText());
                    break;
                }
            }
            assertNotNull(insertedSms);
        } finally {
            if (insertedSms != null) {
                messageDao.delete(insertedSms);
            }
        }
    }

    private static final PDUDecomposer smppDecomposer = new DefaultDecomposer();

    public static void main(String[] args) {
        long id = Long.parseLong("12345") & 0xffffffff;
        System.out.println(Long.toString(id, 16).toUpperCase());
    }
}
