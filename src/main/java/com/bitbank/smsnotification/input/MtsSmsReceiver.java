package com.bitbank.smsnotification.input;

import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;
import com.bitbank.smsnotification.service.IncomingSmsService;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.protocol.IMAPProtocol;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.cxf.helpers.IOUtils;
import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

@Service
public class MtsSmsReceiver {

    private static Logger log = Logger.getLogger(MtsSmsReceiver.class);

    private static final String SOURCE_NAME = MtsSmsReceiver.class.getSimpleName();

    Store store = null;
    IMAPFolder folder = null;

    @Autowired
    private IncomingSmsService incSmsService;

    @Autowired
    private PropertiesConfiguration properties;

    //todo periodicallycheck need to establish connection

    @PostConstruct
    public void initSmtpServer() throws MessagingException {
        if (properties.getBoolean("mts.inputsms.isestablishconnection", false)) {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imap");
            // I assume there is some redundancy here but this didn't cause any problems so far
            props.setProperty("mail.imaps.starttls.enable", "false");
            props.setProperty("mail.imaps.port", "143");
            props.setProperty("mail.imap.ssl.enable", "false");
            props.setProperty("mail.debug", "true");
            props.setProperty("mail.imap.minidletime", "5");

            Session session = Session.getInstance(props);

            store = session.getStore("imap");
            store.connect("192.168.101.25", "testsms", "testsms");

            folder = (IMAPFolder) store.getFolder("INBOX"); // This doesn't work for other email account

            Thread listner = new Thread(
                    new Listener(folder), "ImapListener"
            );

            listner.start();
        }
    }

    @PreDestroy
    public void closeImapConnection() throws MessagingException {
        if (folder != null && folder.isOpen()) {
            folder.close(false);
        }
        if (store != null && store.isConnected()) {
            store.close();
        }
    }

    private class Listener implements Runnable {

        IMAPFolder folder;

        private Listener(IMAPFolder folder) {
            this.folder = folder;
        }

        @Override
        public void run() {
            startListening(folder);
        }
    }

    public void startListening(IMAPFolder imapFolder) {
        // We need to create a new thread to keep alive the connection
        Thread t = new Thread(
                new KeepAliveRunnable(imapFolder), "IdleConnectionKeepAlive"
        );

        t.start();

        while (!Thread.interrupted()) {
            log.debug("Starting IDLE");
            try {
                if (!imapFolder.isOpen()) {
                    try {
                        imapFolder.open(Folder.READ_WRITE);//works only from the second time
                    } catch (MessagingException ex) {
                        imapFolder.open(Folder.READ_WRITE);
                    }
                }
                imapFolder.idle(true);

                log.debug("Got message!!! Cnt unread: "+imapFolder.getUnreadMessageCount());

                //todo multithreading problem
                Message[] mails = imapFolder.getMessages();
                for (Message mail: mails) {
                    IMAPMessage imapMessage = (IMAPMessage) mail;
                    if (imapMessage.isExpunged()) {
                        continue;
                    }

                    StringBuffer sb = new StringBuffer();
                    OutputStream writer = new StringBufferOutputStream(sb);
                    try {
                        IOUtils.copy(imapMessage.getRawInputStream(), writer, imapMessage.getSize());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("EMAIL: "+sb);

                    IncomingSmsMessage sms = new IncomingSmsMessage();
                    try {
                        sms.setFromPhone(imapMessage.getSender().toString());
                        sms.setSmsText(sb.toString());
                        sms.setSourceChannel(SOURCE_NAME);

                        incSmsService.processSms(sms);
                        imapMessage.setFlag(Flags.Flag.DELETED, true);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                }

                if (mails.length > 0) {
                    imapFolder.expunge(mails);
                }

            } catch (MessagingException e) {
                //todo reconnect after failures
                log.warn("Messaging exception during IDLE", e);
                throw new RuntimeException(e);
            }
        }

        // Shutdown keep alive thread
        if (t.isAlive()) {
            t.interrupt();
        }
    }

    /**
     * Runnable used to keep alive the connection to the IMAP server
     *
     * @author Juan Martín Sotuyo Dodero <jmsotuyo@monits.com>
     */
    private static class KeepAliveRunnable implements Runnable {

        private static final long KEEP_ALIVE_FREQ = 300000; // 5 minutes

        private IMAPFolder folder;

        public KeepAliveRunnable(IMAPFolder folder) {
            this.folder = folder;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(KEEP_ALIVE_FREQ);

                    // Perform a NOOP just to keep alive the connection
                    log.debug("Performing a NOOP to keep alive the connection");
                    folder.doCommand(new IMAPFolder.ProtocolCommand() {
                        public Object doCommand(IMAPProtocol p)
                                throws ProtocolException {
                            p.simpleCommand("NOOP", null);
                            return null;
                        }
                    });
                } catch (InterruptedException e) {
                    // Ignore, just aborting the thread...
                } catch (MessagingException e) {
                    // Shouldn't really happen...
                    log.warn("Unexpected exception while keeping alive the IDLE connection", e);
                }
            }
        }
    }

}
