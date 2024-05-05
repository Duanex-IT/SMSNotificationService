package com.bitbank.smsnotification.service;

import com.bitbank.smsnotification.dao.IncomingSmsMessageDao;
import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;
import com.bitbank.smsnotification.output.IninMailSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IncomingSmsServiceImpl implements IncomingSmsService {

    private static Logger log = Logger.getLogger(IncomingSmsServiceImpl.class);

    @Autowired
    private IncomingSmsMessageDao incomingSmsDao;

    @Autowired
    private IninMailSender inin;

    @Override
    public void processSms(IncomingSmsMessage sms) {
        try {
            sms.setDateReceived(new Date());
            inin.sendMail(sms.getFromPhone(), sms.getSmsText());
            sms.setDateProcessed(new Date());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sms.setErrorMessage(e.getMessage());
        }

        incomingSmsDao.save(sms);
    }
}
