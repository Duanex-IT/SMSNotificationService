package com.bitbank.smsnotification.controller;

import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.CustomerSmsBean;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.apache.cxf.feature.Features;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@WebService(endpointInterface = "com.bitbank.smsnotification.controller.CustomersSmsService",
        serviceName = "CustomersSmsService")
public class CustomersSmsServiceImpl implements CustomersSmsService {

    @Autowired
    private SmsMessageDao smsDao;

    @Override
    public List<CustomerSmsBean> getSmsByCustomerId(String customerId, Date dateCreatedFrom, Date dateCreatedTo) {
        List<CustomerSmsBean> customersSms = new ArrayList<>();

        List<SmsMessage> smsList = smsDao.findByCustomerId(customerId, dateCreatedFrom, dateCreatedTo);

        for (SmsMessage sms: smsList) {
            customersSms.add(new CustomerSmsBean(sms));
        }

        return customersSms;
    }
}
