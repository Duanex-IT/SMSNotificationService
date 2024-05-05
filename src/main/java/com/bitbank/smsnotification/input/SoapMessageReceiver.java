package com.bitbank.smsnotification.input;

import com.bitbank.smsnotification.domain.SoapInputMessage;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.domain.message.SmsResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface SoapMessageReceiver {

    @WebMethod
    SmsResponse sendSMSMessage(@WebParam(name = "smsMessage", targetNamespace = "http://input.smsnotification.bitbank.com/") SoapInputMessage message);

}
