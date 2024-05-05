package com.bitbank.smsnotification.output;

import com.bitbank.bitutils.utils.ResourceUtils;
import com.bitbank.notificator.core.sender.EmailSender;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class IninMailSenderImpl implements IninMailSender {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PropertiesConfiguration properties;

    @Autowired
    private ResourceUtils resources;

    @Override
    public void sendMail(String fromPhone, String smsText) throws MessagingException {
        emailSender.sendBusinessMail(properties.getString("inin.from.email"), properties.getString("inin.email"),
                                        resources.getMessage("inin.input.sms.mail.subject"),
                                        resources.getMessage("inin.input.sms.mail.body", new Object[] {fromPhone, smsText}));

        //todo insert call to siebel for statistics
    }

}
