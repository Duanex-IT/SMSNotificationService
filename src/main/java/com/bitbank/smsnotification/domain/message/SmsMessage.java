package com.bitbank.smsnotification.domain.message;

import com.bitbank.smsnotification.domain.MessagePriority;
import com.bitbank.smsnotification.domain.SmsMessageType;
import org.hibernate.annotations.Index;
import org.jsmpp.util.DeliveryReceiptState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ns_sms_request")
@NamedQueries({     //todo query should select marketing sms last
    @NamedQuery(name = "findSentCountFromDate", query = "select count(*) from SmsMessage where dateSent > :fromDate"),
    @NamedQuery(name = "findWaitingForDelivery", query = "from SmsMessage where dateSent is not null and dateDelivered = null order by priority"),
    @NamedQuery(name = "findByCustomerId", query = "from SmsMessage where customerId = :custId")
})
@NamedNativeQueries({
    @NamedNativeQuery(name = "findWaitingForSend", resultClass = SmsMessage.class, query = "select * from ns_sms_request where dateSent is null and retriesCount<20 and type NOT IN ('CLIENT_REQUEST') order by priority, dateReceived for update skip locked"),
    @NamedNativeQuery(name = "findWaitingForSendLimitCount", resultClass = SmsMessage.class, query = "select * from ns_sms_request where dateSent is null and retriesCount<20 and type NOT IN ('CLIENT_REQUEST') and rownum <= :rownum order by priority, dateReceived for update skip locked"),
    @NamedNativeQuery(name = "updateDeliveryStatus", query = "update ns_sms_request set dateDelivered = :deliveryDate, deliveryState = :deliveryState " +
            " where remoteMessageId = :remoteMessageId"),
    @NamedNativeQuery(name = "updateDeliveryStatuses", query = "UPDATE ns_sms_request t1\n"+
            "   SET (t1.deliverystate, t1.datedelivered) = (SELECT t2.finalstatus, t2.donedate\n"+
            "                         FROM ns_delivery_report t2\n"+
            "                        WHERE t1.remotemessageid = t2.remoteid and t1.remotemessageid is not null and rownum<2 )\n"+
            " WHERE t1.deliverystate='UNKNOWN' and dateSent is not null and t1.datedelivered is null and EXISTS (\n"+
            "    SELECT 1\n"+
            "      FROM ns_delivery_report t2\n"+
            "     WHERE t1.remotemessageid = t2.remoteid )")
})
public class SmsMessage implements Serializable {

    public SmsMessage() {
    }

    public SmsMessage(String sourceMethod, String sourceUser, Date dateReceived, MessagePriority priority, SmsMessageType type) {
        this.sourceMethod = sourceMethod;
        this.sourceUser = sourceUser;
        this.dateReceived = dateReceived;
        this.priority = priority;
        this.type = type;
    }

    public SmsMessage(String sourceMethod, String sourceUser, Date dateReceived, MessagePriority priority, SmsMessageType type, String phone, String text) {
        this.sourceMethod = sourceMethod;
        this.sourceUser = sourceUser;
        this.dateReceived = dateReceived;
        this.priority = priority;
        this.type = type;
        this.phone = phone;
        this.smsText = text;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sms_message_seq_gen")
    @SequenceGenerator(allocationSize = 1, name="sms_message_seq_gen", sequenceName="SEQ_SMS_MESSAGE")
    /**
     * Database primary key
     */
    private long messageId;

    @Column(nullable = false)
    private String phone;
    private String fromPhone;
    @Column(nullable = false)
    private String smsText;
    @Enumerated (value = EnumType.STRING)
    @Column(nullable = false)
    private MessagePriority priority;
    @Enumerated (value = EnumType.STRING)
    @Column(nullable = false)
    private SmsMessageType type;

    @Column(nullable = false)
    private String sourceMethod;
    private String sourceUser;
    @Column(length = 4000)
    private String sourceData;

    @Column(nullable = false)
    private Date dateReceived;
    private Date dateSent;
    private Date dateDelivered;
    @Enumerated(value = EnumType.STRING)
    private DeliveryReceiptState deliveryState = DeliveryReceiptState.UNKNOWN;//todo think whether we shoulr depend on jsmpp class
    private int retriesCount;
    @Index(name = "remoteMessageIdIndex")
    /**
     * messageId from smpp channel
     */
    private String remoteMessageId;

    private Date sendBefore;
    private String senderImplementation;

    private String errorMessage;

    private String customerId;

    @Version
    private long version;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public MessagePriority getPriority() {
        return priority;
    }

    public SmsMessageType getType() {
        return type;
    }

    public void setType(SmsMessageType type) {
        this.type = type;
    }

    public void setPriority(MessagePriority priority) {
        this.priority = priority;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSourceMethod() {
        return sourceMethod;
    }

    public void setSourceMethod(String sourceMethod) {
        this.sourceMethod = sourceMethod;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(String sourceUser) {
        this.sourceUser = sourceUser;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getDateDelivered() {
        return dateDelivered;
    }

    public void setDateDelivered(Date dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public DeliveryReceiptState getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(DeliveryReceiptState deliveryState) {
        this.deliveryState = deliveryState;
    }

    public int getRetriesCount() {
        return retriesCount;
    }

    public void incRetriesCount() {
        retriesCount++;
    }

    public void setRetriesCount(int retriesCount) {
        this.retriesCount = retriesCount;
    }

    public String getRemoteMessageId() {
        return remoteMessageId;
    }

    public void setRemoteMessageId(String remoteMessageId) {
        this.remoteMessageId = remoteMessageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSenderImplementation() {
        return senderImplementation;
    }

    public void setSenderImplementation(String senderImplementation) {
        this.senderImplementation = senderImplementation;
    }

    public Date getSendBefore() {
        return sendBefore;
    }

    public void setSendBefore(Date sendBefore) {
        this.sendBefore = sendBefore;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsMessage that = (SmsMessage) o;

        if (retriesCount != that.retriesCount) return false;
        if (dateDelivered != null ? dateDelivered.getTime() != that.dateDelivered.getTime() : that.dateDelivered != null)
            return false;
        if (dateReceived.getTime() != that.dateReceived.getTime()) return false;
        if (dateSent != null ? dateSent.getTime() != that.dateSent.getTime() : that.dateSent != null) return false;
        if (deliveryState != that.deliveryState) return false;
        if (errorMessage != null ? !errorMessage.equals(that.errorMessage) : that.errorMessage != null) return false;
        if (fromPhone != null ? !fromPhone.equals(that.fromPhone) : that.fromPhone != null) return false;
        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null)
            return false;
        if (!phone.equals(that.phone)) return false;
        if (priority != that.priority) return false;
        if (remoteMessageId != null ? !remoteMessageId.equals(that.remoteMessageId) : that.remoteMessageId != null)
            return false;
        if (sendBefore != null ? !sendBefore.equals(that.sendBefore) : that.sendBefore != null) return false;
        if (senderImplementation != null ? !senderImplementation.equals(that.senderImplementation) : that.senderImplementation != null)
            return false;
        if (smsText != null ? !smsText.equals(that.smsText) : that.smsText != null) return false;
        if (sourceData != null ? !sourceData.equals(that.sourceData) : that.sourceData != null) return false;
        if (!sourceMethod.equals(that.sourceMethod)) return false;
        if (sourceUser != null ? !sourceUser.equals(that.sourceUser) : that.sourceUser != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = phone.hashCode();
        result = 31 * result + (fromPhone != null ? fromPhone.hashCode() : 0);
        result = 31 * result + (smsText != null ? smsText.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + sourceMethod.hashCode();
        result = 31 * result + (sourceUser != null ? sourceUser.hashCode() : 0);
        result = 31 * result + (sourceData != null ? sourceData.hashCode() : 0);
        result = 31 * result + dateReceived.hashCode();
        result = 31 * result + (dateSent != null ? dateSent.hashCode() : 0);
        result = 31 * result + (dateDelivered != null ? dateDelivered.hashCode() : 0);
        result = 31 * result + deliveryState.hashCode();
        result = 31 * result + retriesCount;
        result = 31 * result + (remoteMessageId != null ? remoteMessageId.hashCode() : 0);
        result = 31 * result + (sendBefore != null ? sendBefore.hashCode() : 0);
        result = 31 * result + (senderImplementation != null ? senderImplementation.hashCode() : 0);
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SmsMessage{" +
                "messageId=" + messageId +
                ", phone='" + phone + '\'' +
                ", fromPhone='" + fromPhone + '\'' +
                ", smsText='" + smsText + '\'' +
                ", priority=" + priority +
                ", type=" + type +
                ", dateReceived=" + dateReceived +
                ", dateSent=" + dateSent +
                ", dateDelivered=" + dateDelivered +
                ", sendBefore=" + sendBefore +
                ", errorMessage='" + errorMessage + '\'' +
                ", customerId='" + customerId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
