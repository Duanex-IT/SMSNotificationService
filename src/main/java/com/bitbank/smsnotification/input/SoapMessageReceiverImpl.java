package com.bitbank.smsnotification.input;

import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.SoapInputMessage;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.domain.message.SmsResponse;
import com.bitbank.smsnotification.service.SmsSchedulerService;
import org.apache.cxf.feature.Features;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.Date;

@Service(value = "SoapMessageReceiver")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@WebService(endpointInterface = "com.bitbank.smsnotification.input.SoapMessageReceiver",
        serviceName = "SMSNotificationService")
public class SoapMessageReceiverImpl implements SoapMessageReceiver {

    public static final String SOURCE_NAME = "SoapMessageReceiverImpl/sendSMSMessage";
    private static Logger log = Logger.getLogger(SoapMessageReceiverImpl.class);

    @Autowired
    private SmsSchedulerService schedulerService;

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public SmsResponse sendSMSMessage(SoapInputMessage message) {
        log.debug("Got message from SOAP channel:"+message);

        SmsMessage sms = new SmsMessage(SOURCE_NAME, null, new Date(), MessagePriority.NORMAL, SmsMessageType.MB_NOTIFICATION);
        sms.setPhone(message.getPhone());
        sms.setSmsText(message.getSmsText());
        sms.setSourceData(message.getActivityID());
        sms.setCustomerId(message.getCustomerId());

        return schedulerService.addSmsMessageToQueue(sms);
    }
}

