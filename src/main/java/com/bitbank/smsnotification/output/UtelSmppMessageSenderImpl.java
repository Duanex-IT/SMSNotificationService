package com.bitbank.smsnotification.output;

import com.bitbank.bitutils.utils.PhoneUtils;
import com.bitbank.notificator.core.sender.EmailSender;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.exceptions.MessageSenderException;
import com.bitbank.smsnotification.exceptions.SendMessageException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

@Service
public class UtelSmppMessageSenderImpl implements MessageSender {

    public static final String MESSAGESENDER_IMPLEMENTATION_CLASS = "messagesender.implementation.class";
    private static Logger log = Logger.getLogger(UtelSmppMessageSenderImpl.class);

    //smpp fields
    private SMPPSession sessionAlphaNum = new SMPPSession();
    private SMPPSession sessionShort = new SMPPSession();
    private static final DataCoding msgDataCoding = new GeneralDataCoding(Alphabet.ALPHA_UCS2);
    private static final RegisteredDelivery registeredDelivery = new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE);

    //config params names
    private static final String SYSTEM_TYPE = "cp";

    private static final String ALPHANUM_HOST = "messagesender.utel.alphanum.host";
    private static final String ALPHANUM_PORT = "messagesender.utel.alphanum.port";
    private static final String ALPHANUM_SRCADDR = "messagesender.utel.alphanum.source.address";
    private static final String ALPHANUM_SERVERID = "messagesender.utel.alphanum.serverid";
    private static final String ALPHANUM_PASSWORD = "messagesender.utel.alphanum.serverpassword";
    private static final String ALPHANUM_TIMEOUT = "messagesender.utel.alphanum.timeout";

    private static final String SHORT_HOST = "messagesender.utel.short.host";
    private static final String SHORT_PORT = "messagesender.utel.short.port";
    private static final String SHORT_SRCADDR = "messagesender.utel.short.source.address";
    private static final String SHORT_SERVERID = "messagesender.utel.short.serverid";
    private static final String SHORT_PASSWORD = "messagesender.utel.short.serverpassword";
    private static final String SHORT_TIMEOUT = "messagesender.utel.short.timeout";

    @Autowired
    private PropertiesConfiguration properties;

    @Autowired
    private UtelMessageReceiverListener receiverShort;
    @Autowired
    private UtelMessageReceiverListener receiverAlphaNum;

    @Autowired
    private EmailSender emailSender;

    @PostConstruct
    private void initFields() throws IOException {
        checkSessionActive();
    }

    private void bindAlphaNumSession() throws IOException {
        sessionAlphaNum = new SMPPSession();

        log.debug("Binding sessionAlphaNum");
        sessionAlphaNum.setMessageReceiverListener(receiverAlphaNum);
        sessionAlphaNum.connectAndBind(properties.getString(ALPHANUM_HOST), properties.getInt(ALPHANUM_PORT),
            BindType.BIND_TRX, properties.getString(ALPHANUM_SERVERID), properties.getString(ALPHANUM_PASSWORD),
            SYSTEM_TYPE, TypeOfNumber.ALPHANUMERIC, NumberingPlanIndicator.UNKNOWN, null, properties.getInt(ALPHANUM_TIMEOUT));
    }

    private synchronized void bindShortSession() throws IOException {
        sessionShort = new SMPPSession();

        log.debug("Binding sessionShort");
        sessionShort.setMessageReceiverListener(receiverShort);
        sessionShort.connectAndBind(properties.getString(SHORT_HOST), properties.getInt(SHORT_PORT),
            BindType.BIND_TRX, properties.getString(SHORT_SERVERID), properties.getString(SHORT_PASSWORD),
            SYSTEM_TYPE, TypeOfNumber.SUBSCRIBER_NUMBER, NumberingPlanIndicator.ISDN, null, properties.getInt(SHORT_TIMEOUT));
    }

    @PreDestroy
    private void prepareForDestroy() {
        log.debug("Unbinding SMPP sessions");
        sessionAlphaNum.unbindAndClose();
        sessionShort.unbindAndClose();
    }

    @Override
    public String sendMessage(SmsMessage sms) throws SendMessageException, MessageSenderException {
        SMPPSession currSession = null;

//        if (SmsMessageType.MASS_MAIL.equals(sms.getType())) {
            currSession = sessionAlphaNum;
            sms.setFromPhone(properties.getString(ALPHANUM_SRCADDR));
            log.debug("Trying to send message: " + sms + "with session: " + currSession);
            return sendMessageFromSession(sms, currSession, TypeOfNumber.ALPHANUMERIC, NumberingPlanIndicator.UNKNOWN);
//        } else {
//            currSession = sessionShort;
//            sms.setFromPhone(properties.getString(SHORT_SRCADDR));
//            log.debug("Trying to send message: " + sms + "with session: " + currSession);
//            return sendMessageFromSession(sms, currSession, TypeOfNumber.SUBSCRIBER_NUMBER, NumberingPlanIndicator.ISDN);
//        }
    }

    private String sendMessageFromSession(SmsMessage sms, SMPPSession session, TypeOfNumber typeofnumber, NumberingPlanIndicator npi) throws MessageSenderException, SendMessageException {
        String remoteMsgId = null;

        try {
            remoteMsgId = session.submitShortMessage("", typeofnumber, npi,
                    sms.getFromPhone(), TypeOfNumber.UNKNOWN, npi,
                    PhoneUtils.formatWithoutPlus(sms.getPhone()), new ESMClass(), (byte) 0, (byte) sms.getPriority().ordinal(),
                    null, null, registeredDelivery, (byte) 0,
                    msgDataCoding, (byte) 0, sms.getSmsText().getBytes("UTF-16BE"));
            log.debug("Message submitted, message_id is " + remoteMsgId);
        } catch (IOException | ResponseTimeoutException e) {
            log.error("Failed send message", e);
            emailSender.sendErrorMail(e);
            throw new MessageSenderException(e);
        } catch (PDUException | InvalidResponseException | NegativeResponseException e) {
            log.error("Failed send message", e);
            throw new SendMessageException(e);
        }

        return remoteMsgId;
    }

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void checkSessionActive() {
        //todo SessionStateListener
        if (this.getClass().getSimpleName().equals(properties.getString(MESSAGESENDER_IMPLEMENTATION_CLASS))) {
            if (!sessionShort.getSessionState().isBound()) {
                log.debug("sessionShort is not bound!!! Trying to rebind...");
                sessionShort.unbindAndClose();
                try {
                    bindShortSession();
                } catch (IOException e) {
                    log.error("Failed to rebind session", e);
                    emailSender.sendErrorMail(e);
                }
            }
            if (!sessionAlphaNum.getSessionState().isBound()) {
                log.debug("sessionAlphaNum is not bound!!! Trying to rebind...");
                sessionAlphaNum.unbindAndClose();
                try {
                    bindAlphaNumSession();
                } catch (IOException e) {
                    log.error("Failed to rebind session", e);
                    emailSender.sendErrorMail(e);
                }
            }
        }
    }

}
