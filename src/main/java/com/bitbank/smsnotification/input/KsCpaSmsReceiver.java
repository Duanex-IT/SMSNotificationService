package com.bitbank.smsnotification.input;

import com.bitbank.bitutils.utils.ResourceUtils;
import com.bitbank.smsnotification.domain.kyivstar.cpa.Message;
import com.bitbank.smsnotification.domain.kyivstar.cpa.Report;
import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;
import com.bitbank.smsnotification.service.IncomingSmsService;
import org.apache.cxf.feature.Features;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.math.BigInteger;

@Service("KsCpaSmsReceiver")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
public class KsCpaSmsReceiver {

    private static final String SOURCE_NAME = KsCpaSmsReceiver.class.getSimpleName();

    @Autowired
    private IncomingSmsService incSmsService;

    @Autowired
    private ResourceUtils resources;

    @POST
    @Produces(MediaType.APPLICATION_XML_VALUE)
    @Consumes(MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody Report processInputSms(@RequestBody Message cpaMessage) {
        Report response = new Report();
        Report.Status status = new Report.Status();

        IncomingSmsMessage sms = new IncomingSmsMessage();
        sms.setSmsText((String) cpaMessage.getBody().getContent().get(0));//todo check on example
        sms.setSourceChannel(SOURCE_NAME);
        sms.setFromPhone(cpaMessage.getSin());

        try {
            incSmsService.processSms(sms);

            status.setValue("0");
            status.setErrorCode(BigInteger.valueOf(0));
        } catch (Exception e) {
            status.setValue("-1");
            status.setError(e.getMessage());
            status.setErrorCode(BigInteger.valueOf(-1));
        }

        response.setStatus(status);
        return response;
    }

}
