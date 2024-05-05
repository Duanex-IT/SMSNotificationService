package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDao;
import com.bitbank.smsnotification.domain.message.InputSmsDistributionEntity;

import java.util.List;

public interface InputSmsDistributionDao extends AbstractDao<InputSmsDistributionEntity, Long> {
    List<InputSmsDistributionEntity> findAllWithLock();
}
