package com.bitbank.smsnotification.dao;

import com.bitbank.bitutils.dao.AbstractDaoImpl;
import com.bitbank.smsnotification.domain.DeliveryReport;
import org.springframework.stereotype.Repository;

@Repository
public class DeliveryReportDaoImpl extends AbstractDaoImpl<DeliveryReport, Long> implements DeliveryReportDao {
    protected DeliveryReportDaoImpl() {
        super(DeliveryReport.class);
    }
}
