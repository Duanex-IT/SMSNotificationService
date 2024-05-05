package com.bitbank.smsnotification.dao;

import com.bitbank.smsnotification.domain.DeliveryReport;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class DeliveryReportDaoImplTest {

    @Autowired
    private DeliveryReportDao deliveryReportDao;

    @Test
    @NotTransactional
    public void testSaveDelete() {
        int reportsCount = deliveryReportDao.findAll().size();

        DeliveryReport report = new DeliveryReport();
        report.setDoneDate(new Date());
        report.setText("test deliv text");

        deliveryReportDao.save(report);

        try {
            assertNotNull(report.getId());
            assertEquals(reportsCount+1, deliveryReportDao.findAll().size());
            assertEquals(report, deliveryReportDao.findById(report.getId()));
        } finally {
            deliveryReportDao.delete(report);
            assertEquals(reportsCount, deliveryReportDao.findAll().size());
        }
    }

    @Test
    public void testSaveFromRemoteReceipt() {
        int reportsCount = deliveryReportDao.findAll().size();

        DeliveryReceipt receipt = new DeliveryReceipt("recId", 1, 1, new Date(), new Date(), DeliveryReceiptState.DELIVRD, "error", "test receipt text");
        DeliveryReport report = new DeliveryReport(receipt);

        deliveryReportDao.save(report);

        try {
            assertNotNull(report.getId());
            assertEquals(reportsCount+1, deliveryReportDao.findAll().size());
            assertEquals(report, deliveryReportDao.findById(report.getId()));
        } finally {
            deliveryReportDao.delete(report);
            assertEquals(reportsCount, deliveryReportDao.findAll().size());
        }
    }

}
