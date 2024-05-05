package com.bitbank.smsnotification.controller;

import com.bitbank.smsnotification.domain.CustomerSmsBean;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.Date;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface CustomersSmsService {

    @WebMethod
    List<CustomerSmsBean> getSmsByCustomerId(@WebParam(name = "customerId", targetNamespace = "http://smsnotification.bitbank.com/")String customerId,
                                             @WebParam(name = "dateCreatedFrom", targetNamespace = "http://smsnotification.bitbank.com/")Date dateCreatedFrom,
                                             @WebParam(name = "dateCreatedTo", targetNamespace = "http://smsnotification.bitbank.com/")Date dateCreatedTo);
}
