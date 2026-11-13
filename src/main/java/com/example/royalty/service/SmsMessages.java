package com.example.royalty.service;

import com.example.royalty.config.SmsMessageProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Customer-facing SMS texts, read once at startup from the files named in {@link SmsMessageProperties} and
 * held in memory. Editing a file takes effect after a restart.
 *
 * <p>Every file must be readable and non-empty, or the application fails to start. Because nothing is read
 * after startup, building a message cannot fail, which matters in
 * {@link ReceivedMessageService#handleReceivedMessages()}: points are credited before the message is built,
 * and a failure there would credit them again on the next run.
 */
@Service
public class SmsMessages {

    private final SmsMessageProperties properties;

    private String notRegistered;
    private String invalidCode;
    private String usedCode;
    private String pointsEarned;

    public SmsMessages(SmsMessageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void loadAll() {
        notRegistered = load("not-registered", properties.getNotRegistered());
        invalidCode = load("invalid-code", properties.getInvalidCode());
        usedCode = load("used-code", properties.getUsedCode());
        pointsEarned = load("points-earned", properties.getPointsEarned());
    }

    public String notRegistered() {
        return notRegistered;
    }

    public String invalidCode() {
        return invalidCode;
    }

    public String usedCode() {
        return usedCode;
    }

    public String pointsEarned(int points, int total) {
        return pointsEarned
                .replace("{points}", String.valueOf(points))
                .replace("{total}", String.valueOf(total));
    }

    private static String load(String key, Resource resource) {
        if (resource == null) {
            throw new IllegalStateException("sms.message." + key + " is not set");
        }
        try {
            // strip() drops the trailing newline most editors add, which would otherwise be sent in the SMS.
            String text = resource.getContentAsString(StandardCharsets.UTF_8).strip();
            if (text.isEmpty()) {
                throw new IllegalStateException("SMS text sms.message." + key + " is empty: " + resource.getDescription());
            }
            return text;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read SMS text sms.message." + key + ": " + e.getMessage(), e);
        }
    }
}
