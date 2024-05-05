package com.bitbank.smsnotification.domain;

import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ns_delivery_report")
public class DeliveryReport {

    public DeliveryReport() {
    }

    public DeliveryReport(DeliveryReceipt receipt) {
        remoteId = receipt.getId();
        submitted = receipt.getSubmitted();
        delivered = receipt.getDelivered();
        submitDate = receipt.getSubmitDate();
        doneDate = receipt.getDoneDate();
        finalStatus = receipt.getFinalStatus();
        error = receipt.getError();
        text = receipt.getText();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String remoteId;
    private int submitted;
    private int delivered;
    private Date submitDate;
    private Date doneDate;
    @Enumerated(EnumType.STRING)
    private DeliveryReceiptState finalStatus;
    private String error;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSubmitted() {
        return submitted;
    }

    public void setSubmitted(int submitted) {
        this.submitted = submitted;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public DeliveryReceiptState getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(DeliveryReceiptState finalStatus) {
        this.finalStatus = finalStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeliveryReport that = (DeliveryReport) o;

        if (delivered != that.delivered) return false;
        if (submitted != that.submitted) return false;
        if (doneDate != null ? !doneDate.equals(that.doneDate) : that.doneDate != null) return false;
        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        if (finalStatus != that.finalStatus) return false;
        if (submitDate != null ? !submitDate.equals(that.submitDate) : that.submitDate != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (remoteId != null ? !remoteId.equals(that.remoteId) : that.remoteId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = submitted;
        result = 31 * result + delivered;
        result = 31 * result + (submitDate != null ? submitDate.hashCode() : 0);
        result = 31 * result + (doneDate != null ? doneDate.hashCode() : 0);
        result = 31 * result + (finalStatus != null ? finalStatus.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (remoteId != null ? remoteId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeliveryReport{" +
                "id='" + id + '\'' +
                ", submitted=" + submitted +
                ", delivered=" + delivered +
                ", submitDate=" + submitDate +
                ", doneDate=" + doneDate +
                ", finalStatus=" + finalStatus +
                ", error='" + error + '\'' +
                ", text='" + text + '\'' +
                ", remoteId='" + remoteId + '\'' +
                '}';
    }
}
