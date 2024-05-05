package com.bitbank.smsnotification.domain.message;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ns_input_sms")
public class IncomingSmsMessage {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="input_sms_message_seq_gen")
    @SequenceGenerator(allocationSize = 1, name="input_sms_message_seq_gen", sequenceName="SEQ_INPUT_SMS")
    /**
     * Database primary key
     */
    private Long messageId;

    @Column(nullable = false)
    private String fromPhone;
    @Column(length = 3000)
    private String smsText;

    @Column(nullable = false)
    private Date dateReceived;
    private Date dateProcessed;

    @Column(nullable = false)
    private String sourceChannel;
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public Date getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(Date dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IncomingSmsMessage that = (IncomingSmsMessage) o;

        if (dateProcessed != null ? !dateProcessed.equals(that.dateProcessed) : that.dateProcessed != null)
            return false;
        if (!dateReceived.equals(that.dateReceived)) return false;
        if (errorMessage != null ? !errorMessage.equals(that.errorMessage) : that.errorMessage != null) return false;
        if (!fromPhone.equals(that.fromPhone)) return false;
        if (smsText != null ? !smsText.equals(that.smsText) : that.smsText != null) return false;
        if (!sourceChannel.equals(that.sourceChannel)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromPhone.hashCode();
        result = 31 * result + (smsText != null ? smsText.hashCode() : 0);
        result = 31 * result + dateReceived.hashCode();
        result = 31 * result + (dateProcessed != null ? dateProcessed.hashCode() : 0);
        result = 31 * result + sourceChannel.hashCode();
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }
}
