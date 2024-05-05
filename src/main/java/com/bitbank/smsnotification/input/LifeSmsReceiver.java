package com.bitbank.smsnotification.input;

import com.bitbank.smsnotification.service.IncomingSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LifeSmsReceiver {

    @Autowired
    private IncomingSmsService incSmsService;



}
