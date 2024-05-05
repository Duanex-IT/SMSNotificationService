package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDaoImpl;
import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;
import org.springframework.stereotype.Repository;

@Repository
public class IncomingSmsMessageDaoImpl extends AbstractDaoImpl<IncomingSmsMessage, Long> implements IncomingSmsMessageDao {
    protected IncomingSmsMessageDaoImpl() {
        super(IncomingSmsMessage.class);
    }
}
