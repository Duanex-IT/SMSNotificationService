package com.bitbank.smsnotification.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SoapInputMessage {

    private String activityID;

    private String customerId;

    private String phone;

    private String smsText;

    public SoapInputMessage() {
    }

    public SoapInputMessage(String activityID, String customerId, String phone, String smsText) {
        this.activityID = activityID;
        this.customerId = customerId;
        this.phone = phone;
        this.smsText = smsText;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    @Override
    public String toString() {
        return "SoapInputMessage{" +
                "activityID='" + activityID + '\'' +
                ", phone='" + phone + '\'' +
                ", smsText='" + smsText + '\'' +
                '}';
    }
}
