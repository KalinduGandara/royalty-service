package com.example.royalty.util;

import com.example.royalty.service.ReceivedMessageService;
import com.example.royalty.service.SMSService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SMSScheduler {

    private final SMSService smsService;
    private final ReceivedMessageService receivedMessageService;

    public SMSScheduler(SMSService smsService, ReceivedMessageService receivedMessageService) {
        this.smsService = smsService;
        this.receivedMessageService = receivedMessageService;
    }

    @Scheduled(fixedDelayString = "${sms.scheduler.fixedDelay}")
    public void sendUnsentMessages() {
        smsService.sendUnsentMessages();
    }

    @Scheduled(fixedDelayString = "${sms.scheduler.fixedDelay}")
    public void handleReceivedMessages() {
        receivedMessageService.handleReceivedMessages();
    }
}
