package com.example.royalty.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Locations of the customer-facing SMS text files, bound from {@code sms.message.*}.
 *
 * <p>Each value is a Spring resource location: {@code file:/etc/loyalty/sms/invalid-code.txt} for a file on
 * disk that can be edited without a rebuild, or {@code classpath:...} for the defaults bundled in the WAR.
 * Files are read as UTF-8; see {@link com.example.royalty.service.SmsMessages}.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sms.message")
public class SmsMessageProperties {

    /** Sent when the sender's phone number is not a registered customer. */
    private Resource notRegistered;

    /** Sent when the texted code does not exist. */
    private Resource invalidCode;

    /** Sent when the texted code has already been redeemed. */
    private Resource usedCode;

    /** Sent after points are credited. Placeholders: {points}, {total}. */
    private Resource pointsEarned;
}
