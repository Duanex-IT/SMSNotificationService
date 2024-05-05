package com.bitbank.smsnotification.domain;

import com.bitbank.smsnotification.domain.message.SmsMessage;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "ns_ufn_message")
public class UfnMessageEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="ufn_message_seq_gen")
    @SequenceGenerator(allocationSize = 1, name="ufn_message_seq_gen", sequenceName="SEQ_UFN_MESSAGE")
    /**
     * Database primary key
     */
    private long id;

    private Date insertDate;
    private String phone;
    private String smsSender;

    @OneToOne(fetch=FetchType.EAGER)
    private SmsMessage sms;

    /**
     * HPAN
     */
    private String cardHash;
    /**
     * REQAMT
     */
    private String transactionSumm;
    /**
     * RESP
     */
    private String status;
    /**
     * FLD_012
     */
    private String transactionDate;
    /**
     * CARDACCIDC
     */
    private String terminalId;
    private String currency;
    /**
     * NETBAL
     */
    private Double balance;
    private String addressName;
    /**
     * CNVT_CURRENCY
     */
    private String floatSeparator;
    /**
     * UTRNNO
     */
    private String transactionId;
    /**
     * 0=original, 1=reverse
     */
    private String reversal;
    /**
     * TXNCODE
     */
    private String transactionCode;
    private String creditLimit;

    public UfnMessageEntity(Map<String, Object> transactionInfo, String phone, String smsSender) {
        this.insertDate = new Date();
        this.phone = phone;
        this.smsSender = smsSender;

        this.cardHash = (String) transactionInfo.get("HPAN");
        this.transactionSumm = (String) transactionInfo.get("REQAMT");
        this.status = (String) transactionInfo.get("RESP");
        this.transactionDate = (String) transactionInfo.get("FLD_012");
        this.terminalId = (String) transactionInfo.get("CARDACCIDC");
        this.currency = (String) transactionInfo.get("CURRENCY");
        this.balance = (Double) transactionInfo.get("NETBAL");
        this.addressName = (String) transactionInfo.get("ADDRESS_NAME");
        this.floatSeparator = (String) transactionInfo.get("CNVT_CURRENCY");
        this.transactionId = (String) transactionInfo.get("UTRNNO");
        this.reversal = (String) transactionInfo.get("REVERSAL");
        this.transactionCode = (String) transactionInfo.get("TXNCODE");
        this.creditLimit = (String) transactionInfo.get("CREDIT_LIMIT");

    }

    public UfnMessageEntity(Map<String, Object> transactionInfo, SmsMessage sms, String smsSender) {
        this(transactionInfo, sms.getPhone(), smsSender);
        this.sms = sms;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsSender() {
        return smsSender;
    }

    public void setSmsSender(String smsSender) {
        this.smsSender = smsSender;
    }

    public SmsMessage getSms() {
        return sms;
    }

    public void setSms(SmsMessage sms) {
        this.sms = sms;
    }

    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    public String getTransactionSumm() {
        return transactionSumm;
    }

    public void setTransactionSumm(String transactionSumm) {
        this.transactionSumm = transactionSumm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getFloatSeparator() {
        return floatSeparator;
    }

    public void setFloatSeparator(String floatSeparator) {
        this.floatSeparator = floatSeparator;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReversal() {
        return reversal;
    }

    public void setReversal(String reversal) {
        this.reversal = reversal;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }
}
