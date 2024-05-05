package com.bitbank.smsnotification.input;

import com.bitbank.smsnotification.dao.InputSmsDistributionDao;
import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import com.bitbank.smsnotification.domain.message.InputSmsDistributionEntity;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SmsFromDbtableLoader {

    private static Logger log = Logger.getLogger(SmsFromDbtableLoader.class);

    private static final String SOURCE_METHOD = "SmsFromDbtableLoader";

    @Autowired
    private InputSmsDistributionDao smsDistributionDao;

    @Autowired
    private SmsMessageDao smsDao;

    @Scheduled(initialDelay = 10*1000, fixedRate=600*1000)
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void loadFromDatabase() {
        log.debug("STARTED LOADFROMDATABASE");
        //todo multithreading issue
        List<InputSmsDistributionEntity> distribSmss = smsDistributionDao.findAllWithLock();//todo query with date
        log.debug("Moving input messages to sms queue. Messages count = "+distribSmss.size());

        for (InputSmsDistributionEntity entity: distribSmss) {
            //save to sms queue
            SmsMessage sms = new SmsMessage(SOURCE_METHOD, null, new Date(), MessagePriority.NORMAL, SmsMessageType.MASS_MAIL, entity.getPhone(), entity.getSmsText());

            try {
                smsDao.save(sms);
                smsDistributionDao.delete(entity);
            } catch (HibernateException e) {
                log.error(e);
                entity.setFailureText(e.getMessage());
                smsDistributionDao.update(entity);
            }
        }
    }

}
