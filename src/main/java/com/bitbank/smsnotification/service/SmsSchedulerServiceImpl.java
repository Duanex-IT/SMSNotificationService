package com.bitbank.smsnotification.service;

import com.bitbank.notificator.core.sender.EmailSender;
import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.message.*;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Service
public class SmsSchedulerServiceImpl implements SmsSchedulerService {

    private static Logger log = Logger.getLogger(SmsSchedulerServiceImpl.class);

    protected static final String STOP_FILE_NAME = "stop_sending_sms";

    @Autowired
    private EmailSender emailSender;

    @Resource
    private SmsMessageDao smsMessageDao;

    @Autowired
    private SmsProcessorService smsProcessorService;

    @Value(value = "${bit_config}")
    private String configFolder;

    @Override
    public SmsResponse addSmsMessageToQueue(SmsMessage sms) {
        return addSmsMessageToQueue(sms, false);
    }

    @Override
    public SmsResponse addSmsMessageToQueue(SmsMessage sms, boolean sendImmediately) {
        SmsResponse response = new SmsResponse();
        try {
            smsMessageDao.save(sms);
            response.setMessageID(sms.getMessageId());

            if (sendImmediately) {
                smsProcessorService.sendScheduledSmsMessages();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setErrorCode("1");
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public void addSmsMessagesToQueue(List<SmsMessage> smsMessages) throws HibernateException {
        addSmsMessagesToQueue(smsMessages, false);
    }

    @Override
    public void addSmsMessagesToQueue(List<SmsMessage> smsMessages, boolean sendImmediately) throws HibernateException {
        if (!CollectionUtils.isEmpty(smsMessages)) {
            smsMessageDao.save(smsMessages);

            if (sendImmediately) {
                smsProcessorService.sendScheduledSmsMessages();
            }
        }
    }

}

