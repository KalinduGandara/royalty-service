package com.example.royalty.service;

import com.example.royalty.modal.Customer;
import com.example.royalty.modal.Product;
import com.example.royalty.modal.ReceivedMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service

public class ReportService {

    private final ReceivedMessageService receivedMessageService;
    private final CustomerService customerService;
    private final ProductService productService;

    public ReportService(ReceivedMessageService receivedMessageService, CustomerService customerService, ProductService productService) {
        this.receivedMessageService = receivedMessageService;
        this.customerService = customerService;
        this.productService = productService;
    }

    public List<String[]> generateMaster(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<ReceivedMessage> messages = receivedMessageService.getAllFilterByReceivedTimeAndStatus(startDateTime, endDateTime, "processed");
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"SMS receipt Date", "TP Number", "Cust Name", "SMS Code", "Mat. Desc", "Points Earned", "City", "Ditrict", "Province", "Sales person Territory", "Region", "Assigned CMDE"});
        for (ReceivedMessage message : messages) {
            Customer customer = customerService.findById(message.getCid());
            Product product = productService.findById(message.getProductId());
            if (customer == null || product == null) {
                continue;
            }

            rows.add(new String[]{
                    message.getReceivedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    customer.getTpNumber(),
                    customer.getName(),
                    message.getMessage(),
                    product.getDescription(),
                    String.valueOf(product.getPoints()),
                    customer.getCity(),
                    customer.getDistrict(),
                    customer.getProvince(),
                    customer.getSalesPersonTerritory(),
                    customer.getRegion(),
                    customer.getAssignedCMDE()
            });
        }

        return rows;
    }
}
