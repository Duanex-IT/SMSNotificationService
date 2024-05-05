package com.bitbank.smsnotification.domain.message;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ns_sms_input_distribution")
public class InputSmsDistributionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="sms_input_distribution_seq_gen")
    @SequenceGenerator(allocationSize = 1, name="sms_input_distribution_seq_gen", sequenceName="SEQ_SMS_INPUT_DISTRIBUTION")
    /**
     * Database primary key
     */
    private Long id;

    private String smsText;

    private String phone;

    @Column(nullable = true)
    private Date sendDate;

    private String failureText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getFailureText() {
        return failureText;
    }

    public void setFailureText(String failureText) {
        this.failureText = failureText;
    }
}
