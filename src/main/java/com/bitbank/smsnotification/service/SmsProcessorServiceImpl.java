package com.bitbank.smsnotification.service;

import com.bitbank.notificator.core.sender.EmailSender;
import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.domain.message.SmsResponse;
import com.bitbank.smsnotification.exceptions.MessageSenderException;
import com.bitbank.smsnotification.exceptions.SendMessageException;
import com.bitbank.smsnotification.output.MessageSender;
import com.bitbank.smsnotification.output.MessageSenderFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SmsProcessorServiceImpl implements SmsProcessorService {

    private static Logger log = Logger.getLogger(SmsProcessorServiceImpl.class);

    protected static final String STOP_FILE_NAME = "stop_sending_sms";

    @Autowired
    private EmailSender emailSender;

    @Resource
    private SmsMessageDao smsMessageDao;

    @Autowired
    private MessageSenderFactory messageSenderFactory;

    @Value("${bit_config}")
    private String configFolder;

    @Autowired
    private PropertiesConfiguration properties;

    @Override
    @Async
    @Scheduled(initialDelay = 30*1000, fixedRate=150*1000)
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, noRollbackFor = Exception.class)
    public void sendScheduledSmsMessages() {
        log.debug("STARTED SENDSCHEDULEDSMSMESSAGES");
        try {
            if (smsSendingEnabled()) {
                MessageSender sender = messageSenderFactory.getConcreteMessageSender();

                if (sender != null) {
                    List<SmsMessage> smsMessages = smsMessageDao.findWaitingForSend(properties.getInt("utel.smpp.capacity.daily"));
                    log.debug("waiting messages: "+smsMessages.size());

                    try {
                        for (SmsMessage sms: smsMessages) {
                            if (sms.getRetriesCount()>=10) {
                                if (sms.getRetriesCount()==10) {
                                    emailSender.sendMail("can't send sms with id="+sms.getMessageId(), sms.toString());
                                    sms.incRetriesCount();
                                    smsMessageDao.update(sms);//todo update(sms) is in two places for two cases -- also in sendMessage()
                                }
                            } else {
                                sendMessage(sms, sender);
                            }
                        }
                    } catch (MessageSenderException e) {
                        log.error("Sms sendins stopped for current selection");
                        emailSender.sendErrorMail(e);
                    }
                } else {
                    log.fatal("Sender not found!!!");
                    emailSender.sendMail("Sender not found", "Please, check the 'messagesender.implementation.class' property value in smsnotification.properties file");
                }

            } else {
                log.info("Sms sending option disabled. To turn on sending sms rename file "+configFolder+STOP_FILE_NAME);
            }
        } catch (Exception e) {
            log.error("Error occured", e);
            throw e;
        }
    }

    @Override
    public boolean smsSendingEnabled() {
        File file = new File(configFolder + STOP_FILE_NAME);
        return !file.exists();
    }

    @Override
    public void sendConcreteSmsMessage(Long smsId) {

    }

    private SmsResponse sendMessage(SmsMessage sms, MessageSender sender) throws MessageSenderException {
        SmsResponse response = new SmsResponse();

        if (sender != null) {
            long minSendDuration = properties.getLong("utel.smpp.capacity.message.duration.millis");
            long curTime = Calendar.getInstance().getTimeInMillis();

            sms.incRetriesCount();
            String remoteId;
            try {
                remoteId = sender.sendMessage(sms);

                sms.setDateSent(new Date());
                sms.setSenderImplementation(sender.getClass().getSimpleName());
                sms.setRemoteMessageId(remoteId);
            } catch (SendMessageException e) {
                if (e.getMessage().length() > 255) {
                    sms.setErrorMessage(e.getMessage().substring(0, 254));
                } else {
                    sms.setErrorMessage(e.getMessage());
                }
            } finally {
                smsMessageDao.update(sms);

                long timeLasted = Calendar.getInstance().getTimeInMillis()-curTime;
                if (timeLasted < minSendDuration) {
                    try {
                        Thread.currentThread().sleep(minSendDuration - timeLasted);
                    } catch (InterruptedException e) {
                        log.error(e);
                    }
                }
            }
        } else {
            response.setErrorMessage("Sender is null");
        }

        return response;
    }


    @Override
    @Scheduled(initialDelay = 60*1000, fixedRate=900*1000)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateDeliveryStatuses() {
        log.debug("STARTED UPDATEDELIVERYSTATUSES");
        try {
            int updatedCount = smsMessageDao.updateDeliveryStatuses();
            log.debug("update delivery statuses: "+updatedCount);
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            emailSender.sendErrorMail(e);
        }
    }


}
