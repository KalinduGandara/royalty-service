package com.example.royalty.service;

import com.example.royalty.modal.Customer;
import com.example.royalty.modal.Product;
import com.example.royalty.modal.User;
import com.example.royalty.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public boolean create(Customer customer) {
        if (customerRepository.findByNic(customer.getNic()) == null) {
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

    public boolean update(long id, Customer customer) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isEmpty()) {
            return false;
        }
        Customer customer1 = customerOptional.get();
        customer1.setName(customer.getName());
        customer1.setNic(customer.getNic());
        customer1.setPhone(customer.getPhone());
        customer1.setAddress(customer.getAddress());
        customer1.setArea(customer.getArea());
        customer1.setPoints(customer.getPoints());
        customer1.setNotes(customer.getNotes());
        customerRepository.save(customer1);
        return true;
    }

    public Customer getById(long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElse(null);
    }
}
