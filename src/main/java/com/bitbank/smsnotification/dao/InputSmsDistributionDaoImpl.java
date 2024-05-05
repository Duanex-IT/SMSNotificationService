package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDaoImpl;
import com.bitbank.smsnotification.domain.message.InputSmsDistributionEntity;
import org.hibernate.LockOptions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InputSmsDistributionDaoImpl extends AbstractDaoImpl<InputSmsDistributionEntity, Long> implements InputSmsDistributionDao {
    protected InputSmsDistributionDaoImpl() {
        super(InputSmsDistributionEntity.class);
    }

    @Override
    public List<InputSmsDistributionEntity> findAllWithLock() {
        return getCurrentSession().createQuery("from " + InputSmsDistributionEntity.class.getName()).
                setLockOptions(LockOptions.UPGRADE).list();
    }

}
