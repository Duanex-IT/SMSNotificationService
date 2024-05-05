package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDaoImpl;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.jsmpp.util.DeliveryReceiptState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public class SmsMessageDaoImpl extends AbstractDaoImpl<SmsMessage, Long> implements SmsMessageDao {
    protected SmsMessageDaoImpl() {
        super(SmsMessage.class);
    }

    @Override
    public List<SmsMessage> findWaitingForSend() {
        return getCurrentSession().getNamedQuery("findWaitingForSend").list();
    }

    @Override
    public List<SmsMessage> findWaitingForSend(int dailyLimit) {
        //todo daily limit
        return getCurrentSession().getNamedQuery("findWaitingForSendLimitCount").setInteger("rownum", /*(int) (*/dailyLimit/* - findSentLastDayCount())*/).list();
    }

    @Override
    public long findSentLastDayCount() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return (Long) getCurrentSession().getNamedQuery("findSentCountFromDate").setParameter("fromDate", cal.getTime()).iterate().next();
    }

    @Override
    public int updateDeliveryStatus(String messageId, Date deliveryDate, DeliveryReceiptState finalStatus) {
        return getCurrentSession().getNamedQuery("updateDeliveryStatus").
                setString("deliveryState", finalStatus.name()).
                setDate("deliveryDate", deliveryDate).
                setString("remoteMessageId", messageId).
                executeUpdate();
    }

    @Override
    public int updateDeliveryStatuses() {
        return getCurrentSession().getNamedQuery("updateDeliveryStatuses").executeUpdate();
    }

    @Override
    public List<SmsMessage> findWaitingForDeliveryReport() {
        return getCurrentSession().getNamedQuery("findWaitingForDelivery").setLockOptions(LockOptions.UPGRADE).list();
    }

    @Override
    public List<SmsMessage> findByCustomerId(String customerId, Date dateReceivedFrom, Date dateReceivedTo) {
        Criteria criteria = getCurrentSession().createCriteria(SmsMessage.class).
                add(Restrictions.eq("customerId", customerId)).
                addOrder(Property.forName("messageId").desc());

        if (dateReceivedFrom != null) {
            criteria.add( Restrictions.ge("dateReceived", dateReceivedFrom ) );
        }
        if (dateReceivedTo != null) {
            criteria.add( Restrictions.le("dateReceived", dateReceivedTo ) );
        }

        return criteria.list();
    }
}
