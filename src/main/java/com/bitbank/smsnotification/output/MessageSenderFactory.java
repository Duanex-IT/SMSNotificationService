package com.bitbank.smsnotification.output;

import com.bitbank.bitutils.utils.ResourceUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderFactory {

    @Autowired
    private PropertiesConfiguration properties;

    @Autowired
    private UtelSmppMessageSenderImpl utelSmppSender;

    @Autowired
    private StubMessageSenderImpl stubSender;

    /**
     * Gets the implementation of message sender defined in config
     * @return
     */
    public MessageSender getConcreteMessageSender() {
        String currentSender = properties.getString("messagesender.implementation.class");

        if (UtelSmppMessageSenderImpl.class.getSimpleName().equals(currentSender)) {
            utelSmppSender.checkSessionActive();
            return utelSmppSender;
        } else if (StubMessageSenderImpl.class.getSimpleName().equals(currentSender)) {
            return stubSender;
        } else {
            return null;
        }
    }

    /**
     * If we used concrete message sender for sending message, than we should use same sender to get delivery report
     * @return
     */
    public MessageSender getMessageSenderForDeliveryReport() {
        return null;
    }

}
