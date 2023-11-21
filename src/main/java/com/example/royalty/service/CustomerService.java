package com.example.royalty.service;

import com.example.royalty.modal.Customer;
import com.example.royalty.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public boolean create(Customer customer) {
        if (validateCustomer(customer)) {
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public void createBulk(List<String[]> rows) {
        for (String[] row : rows) {
            Customer customer = new Customer();
            customer.setName(row[0]);
            customer.setNic(row[1]);
            customer.setPhone(row[2]);
            customer.setAddress(row[3]);
            customer.setArea(row[4]);
            customer.setPoints(Integer.parseInt(row[5]));
            customer.setNotes(row[6]);
            customerRepository.save(customer);
        }
    }

    private boolean validateCustomer(Customer customer) {
        return customer.getName().isEmpty() || customer.getNic().isEmpty() || customer.getPhone().isEmpty() || customer.getAddress().isEmpty() ;
    }

}
