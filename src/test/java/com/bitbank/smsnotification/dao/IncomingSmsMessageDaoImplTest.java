package com.bitbank.smsnotification.dao;

import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class IncomingSmsMessageDaoImplTest {

    @Autowired
    private IncomingSmsMessageDao incSmsDao;

    @Test
    public void testBasic() {
        IncomingSmsMessage sms = new IncomingSmsMessage();
        sms.setDateReceived(new Date());
        sms.setFromPhone("4532");
        sms.setSmsText("sfg");
        sms.setSourceChannel("dao testBasic");

        long initCnt = incSmsDao.count();
        incSmsDao.save(sms);

        try {
            assertEquals(initCnt+1, incSmsDao.count());
        } finally {
            incSmsDao.delete(sms);
        }
    }

}
