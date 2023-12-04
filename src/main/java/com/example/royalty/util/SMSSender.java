package com.example.royalty.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SMSSender {

    public static void main(String[] args) {
        try {
            // API URL
            String apiUrl = "https://richcommunication.dialog.lk/api/sms/inline/send";

            // API Key
            String apiKey = "key";

            // Destination phone number

            String destinationNumber = "94716865321"; // Replace with the actual phone number

            // SMS message
            String message = "Hello, This is a test message from LeadSquared. Kindly inform Glen Paul if you receive this message";

            // Encode message for URL
            String encodedMessage = java.net.URLEncoder.encode(message, StandardCharsets.UTF_8.toString());

            // Construct the final API request URL
            String requestUrl = apiUrl + "?q=" + apiKey + "&destination=" + destinationNumber + "&message=" + encodedMessage;

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
            System.out.println("Response Message: " + response.toString());

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
