package com.example.royalty.util;

import com.example.royalty.service.SMSService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SMSScheduler {

    private final SMSService smsService;

    public SMSScheduler(SMSService smsService) {
        this.smsService = smsService;
    }

    @Scheduled(fixedDelay = 10000)
    public void sendUnsentMessages() {
        smsService.sendUnsentMessages();
    }
}
