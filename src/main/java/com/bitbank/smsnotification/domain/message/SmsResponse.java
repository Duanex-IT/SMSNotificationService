package com.bitbank.smsnotification.domain.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Responce object for SOAP request with incoming sms
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMSResponseType", propOrder = {
        "messageID",
        "errorCode",
        "errorMessage"
})
@XmlRootElement(name = "SMSResponse")
public class SmsResponse implements Serializable {
    @XmlElement(name = "messageID", required = true, nillable = false)
    private long messageID;
    @XmlElement(name = "errorCode", required = true, nillable = true)
    private String errorCode;
    @XmlElement(name = "errorMessage", required = true, nillable = true)
    private String errorMessage;

    public long getMessageID() {
        return messageID;
    }
    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
