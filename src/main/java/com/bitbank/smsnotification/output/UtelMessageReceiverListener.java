package com.bitbank.smsnotification.output;

import com.bitbank.notificator.core.sender.EmailSender;
import com.bitbank.smsnotification.dao.DeliveryReportDao;
import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.DeliveryReport;
import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.DefaultDecomposer;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.jsmpp.util.PDUDecomposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Date;

@Service
@Scope(value = "prototype")
public class UtelMessageReceiverListener implements MessageReceiverListener {

    private static Logger log = Logger.getLogger(UtelMessageReceiverListener.class);

    private static final String SOURCE_METHOD_NAME = "UtelMessageReceiverListener";

    private static final PDUDecomposer smppDecomposer = new DefaultDecomposer();

    @Autowired
    private SmsMessageDao smsMessageDao;

    @Autowired
    private DeliveryReportDao deliveryReportDao;

    @Autowired
    private EmailSender emailSender;

    @Override
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        if (deliverSm.isSmscDeliveryReceipt()) {
            // this is delivery receipt
            try {
                DeliveryReceipt delReceipt = smppDecomposer.deliveryReceipt(deliverSm.getShortMessage());
                log.debug("Delivery text is: "+delReceipt.getText());

                try {
                    deliveryReportDao.save(new DeliveryReport(delReceipt));//todo need to add current server time
                } catch (HibernateException e) {
                    log.error("cant save delivery report", e);
                    emailSender.sendErrorMail(e);
                    throw new ProcessRequestException("Can't save delivery report. Please, try later", SMPPConstant.STAT_ESME_RDELIVERYFAILURE);
                }
            } catch (InvalidDeliveryReceiptException e) {
                log.error("cant process delivery report since error parse found", e);
                throw new ProcessRequestException("Rejecting delivery receipt since error parse found", SMPPConstant.STAT_ESME_RINVDFTMSGID);
            }
        } else {
            // this is ordinary message, add to database
            String smsText = new String(deliverSm.getShortMessage(), Charset.forName("UTF-16"));
            log.debug("Received sms: "+smsText+", from phone: "+deliverSm.getSourceAddr());
            SmsMessage incomingSms = new SmsMessage(SOURCE_METHOD_NAME, deliverSm.getDestAddress(), new Date(), MessagePriority.NORMAL, SmsMessageType.CLIENT_REQUEST);
            incomingSms.setSmsText(smsText);
            //incomingSms.setRemoteMessageId(deliverSm.get);//todo
            incomingSms.setPhone(deliverSm.getSourceAddr());
            incomingSms.setDeliveryState(DeliveryReceiptState.ACCEPTD);
            smsMessageDao.save(incomingSms);
            //todo notify message came
        }
    }

    @Override
    public void onAcceptAlertNotification(AlertNotification alertNotification) {
        log.info("Received alert with commandId = "+alertNotification.getCommandId());
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
        log.info("Received dataSm with commandId = " + dataSm.getCommandId());
        return null;
    }
}
