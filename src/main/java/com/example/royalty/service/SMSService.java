package com.example.royalty.service;

import com.example.royalty.modal.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SMSService {
    private final MessageService messageService;
//    private static final String API_URL = "https://richcommunication.dialog.lk/api/sms/inline/send";
    //mock server
    private static final String API_URL = "http://localhost:8080/api/sms";

    @Value("${sms.api.key}")
    private String API_KEY;

    public SMSService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void sendUnsentMessages() {
        List<Message> unsentMessages = messageService.getUnsentMessages();

        for (Message message : unsentMessages) {
            sendSMS(message);
        }
    }

    private void sendSMS(Message message) {
        try {
            System.out.println("Sending SMS: " + message);

            // Destination phone number
            String destinationNumber = message.getPhone().replaceAll("\\s", "");;

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
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            // Close the BufferedReader
            reader.close();
            // Print the response
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Message: " + response);
            // TODO: Handle the response as needed (update database, log, etc.)

            message.setSend(true);
            message.setSendTime(LocalDateTime.now());
            messageService.update(message);

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

