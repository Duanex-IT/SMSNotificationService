package com.bitbank.smsnotification.domain;

import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.jsmpp.util.DeliveryReceiptState;

import java.util.Date;

public class CustomerSmsBean {

    private String phone;
    private String fromPhone;
    private String smsText;
    private SmsMessageType type;
    private String siebelId;
    private Date dateReceived;
    private Date dateSent;
    private Date dateDelivered;
    private DeliveryReceiptState deliveryState;
    private String errorMessage;
    private String customerId;

    public CustomerSmsBean(SmsMessage sms) {
        phone = sms.getPhone();
        fromPhone = sms.getFromPhone();
        smsText = sms.getSmsText();
        type = sms.getType();
        siebelId = sms.getSourceData();
        dateReceived = sms.getDateReceived();
        dateSent = sms.getDateSent();
        dateDelivered = sms.getDateDelivered();
        deliveryState = sms.getDeliveryState();
        errorMessage = sms.getErrorMessage();
        customerId = sms.getCustomerId();
    }

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

    public SmsMessageType getType() {
        return type;
    }

    public void setType(SmsMessageType type) {
        this.type = type;
    }

    public String getSiebelId() {
        return siebelId;
    }

    public void setSiebelId(String siebelId) {
        this.siebelId = siebelId;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
