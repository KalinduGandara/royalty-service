package com.example.royalty.service;

import com.example.royalty.modal.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SMSService {

    Logger logger = LoggerFactory.getLogger(SMSService.class);
    private final MessageService messageService;
//    private static final String API_URL = "https://richcommunication.dialog.lk/api/sms/inline/send";
    @Value("${sms.api.url}")
    private String API_URL;

    @Value("${sms.api.key}")
    private String API_KEY;

    // Without both of these the sender blocks forever on a gateway that accepts
    // the connection and then stops answering: the JDK defaults to no timeout at
    // all, so no exception is ever raised and the scheduler thread never returns.
    @Value("${sms.api.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${sms.api.read-timeout-ms:10000}")
    private int readTimeoutMs;

    @Value("${sms.api.max-consecutive-failures:5}")
    private int maxConsecutiveFailures;

    public SMSService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void sendUnsentMessages() {
        List<Message> unsentMessages = messageService.getUnsentMessages();

        int consecutiveFailures = 0;
        for (int i = 0; i < unsentMessages.size(); i++) {
            if (sendSMS(unsentMessages.get(i))) {
                consecutiveFailures = 0;
                continue;
            }
            consecutiveFailures++;
            if (consecutiveFailures >= maxConsecutiveFailures) {
                // The gateway looks down. Leave the rest queued for the next run
                // instead of spending a timeout on every message in the backlog.
                logger.warn("Giving up this run after {} consecutive failures, {} message(s) still queued.",
                        consecutiveFailures, unsentMessages.size() - i - 1);
                return;
            }
        }
    }

    private boolean sendSMS(Message message) {
        HttpURLConnection connection = null;
        try {
            logger.info("Sending SMS: {}", message);

            // Destination phone number
            String destinationNumber = message.getPhone().replaceAll("\\s", "");

            // SMS message
            String smsMessage = message.getMessage();

            // Encode message for URL
            String encodedMessage = java.net.URLEncoder.encode(smsMessage, StandardCharsets.UTF_8);

            String encodedNumber = java.net.URLEncoder.encode(destinationNumber, StandardCharsets.UTF_8);

            // Construct the final API request URL
            String requestUrl = API_URL + "?q=" + API_KEY + "&destination=" + encodedNumber + "&message=" + encodedMessage;

            // Create a URL object
            URL url = new URL(requestUrl);

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode > 299) {
                // Drain the error stream so the socket can go back to the pool.
                logger.error("Gateway rejected message {} with status {}: {}",
                        message.getId(), responseCode, readBody(connection.getErrorStream()));
                return false;
            }

            // Read the response from the API
            String response = readBody(connection.getInputStream());
            logger.debug("Response Code: {} --- Response Message: {}", responseCode, response);

            message.setSend(true);
            message.setSendTime(LocalDateTime.now());
            messageService.update(message);
            return true;
        } catch (IOException e) {
            // Timeouts land here too, so a sick gateway costs one timeout per
            // message instead of stopping delivery until the next restart.
            logger.error("Failed to send message {} to {}: {}",
                    message.getId(), message.getPhone(), e.toString());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readBody(InputStream stream) {
        if (stream == null) {
            return "";
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            logger.warn("Could not read the gateway response: {}", e.toString());
        }
        return response.toString();
    }
}
