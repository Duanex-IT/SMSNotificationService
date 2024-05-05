package com.bitbank.smsnotification.utils;

import com.bitbank.bitutils.domain.soapheader.cxf.Security;
import com.bitbank.bitutils.exceptions.EmptyUrlException;
import com.bitbank.bitutils.utils.SoapCxfUtils;
import com.bitbank.siebel.phonebyhash.BITSpcUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import static com.bitbank.bitutils.utils.SoapCxfUtils.getSecurityHeader;

@Service
public class SiebelUtils {

    private static final Logger log = Logger.getLogger(SiebelUtils.class);
    public static final String CONFIG_ADDRESS_URL = "siebel.soap.url";
    public static final String WEB_NAMESPACE = "http://siebel.com/CustomUI";
    public static final String WEB_NAME_GETOPTY = "BIT_spcGet_spcOpty";
    public static final String CONFIG_USERNAME = "siebel.soap.login";
    public static final String CONFIG_PASSWORD = "siebel.soap.password";

    @Autowired
    private PropertiesConfiguration properties;

    public String getPhoneByCardHash(String hash) throws JAXBException, EmptyUrlException {
        if ("true".equals(properties.getString("siebel.getPhoneByCardHash.usestub"))) {
            return "380919486030";
        }
        // Создать сервер Soap.
        BITSpcUtils port = SoapCxfUtils.getServiceSoap(properties.getString(CONFIG_ADDRESS_URL)
                , BITSpcUtils.class, new QName(WEB_NAMESPACE, WEB_NAME_GETOPTY));

        Security header = getSecurityHeader(properties.getString(CONFIG_USERNAME), properties.getString(CONFIG_PASSWORD));
        // Формирование SOAP Header.
        SoapCxfUtils.addSoapHeader((BindingProvider) port, SoapCxfUtils.getHeaderSecurity(header));

        Holder<String> errorCode = new Holder<>();
        Holder<String> errorMessage = new Holder<>();
        Holder<String> phone = new Holder<>();

        try {
            port.getMobilePhoneByCardNum(hash, errorCode, errorMessage, phone);
        } catch (Exception e) {
            log.error("Exception", e);
        }

        return phone.value;
    }

}
