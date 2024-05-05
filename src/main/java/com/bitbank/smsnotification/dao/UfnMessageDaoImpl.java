package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDaoImpl;
import com.bitbank.smsnotification.domain.UfnMessageEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UfnMessageDaoImpl extends AbstractDaoImpl<UfnMessageEntity, Long> implements UfnMessageDao {
    protected UfnMessageDaoImpl() {
        super(UfnMessageEntity.class);
    }
}
