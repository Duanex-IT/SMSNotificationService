package com.bitbank.smsnotification.utils;

import com.bitbank.bitutils.exceptions.EmptyUrlException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;
import javax.xml.ws.soap.SOAPFaultException;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class SiebelUtilsTest {

    @Autowired
    private SiebelUtils siebelUtils;

    @Test
    public void testSiebelGetPhoneByHash() throws JAXBException, EmptyUrlException {
        assertNotNull(siebelUtils.getPhoneByCardHash("cca4fe59d768849e563734c384a5a02d"));
    }

    @Ignore //TODO по не верному номеру возникает исключение
    @Test
    public void testSiebelGetPhoneByBadHash() throws JAXBException, EmptyUrlException {
        // Получить номер телефона по не корректному hash-card number.
        String phone = siebelUtils.getPhoneByCardHash("682f937deff533c0808f52652a72810");
        assertNotNull(phone);
    }

    @Ignore
    @Test(expected = SOAPFaultException.class)
    public void testSiebelGetPhoneByHashNotFound() throws JAXBException, EmptyUrlException {
        //todo test isnt working for "true".equals(properties.getString("siebel.getPhoneByCardHash.usestub"))
        siebelUtils.getPhoneByCardHash("123454321");
    }

}
