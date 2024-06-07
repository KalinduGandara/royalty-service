package com.example.royalty.service;

import com.example.royalty.dao.RedeemPointsDAO;
import com.example.royalty.modal.Customer;
import com.example.royalty.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    Logger logger = LoggerFactory.getLogger(CustomerService.class);


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

    public int createBulk(List<String[]> rows) {
        List<String[]> incompleteRows = new ArrayList<>();
        for (String[] row : rows) {
            if (row[0].isEmpty() || row[1].isEmpty() || row[2].isEmpty()) {
                incompleteRows.add(row);
                continue;
            }
            Customer customer = customerRepository.findByNic(row[1]);
            if (customer != null) {
                customer.setPoints(customer.getPoints() + Integer.parseInt(row[5]));

            }else {
                customer = new Customer();
                customer.setNic(row[1]);
                customer.setPoints(Integer.parseInt(row[5]));
            }
            customer.setName(row[0]);
            customer.setPhone(row[2]);
            customer.setAddress(row[3]);
            customer.setArea(row[4]);
            customer.setNotes(row[6]);
            customerRepository.save(customer);
        }
        for (String[] incompleteRow : incompleteRows) {
            logger.error("Incomplete row: {}", (Object) incompleteRow);
        }
        return incompleteRows.size();
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

    public Customer getByPhone(String phoneNumber) {
        return customerRepository.findByPhone(phoneNumber);
    }

    public boolean redeemPoints(RedeemPointsDAO dao) {
        List<Customer> customers = customerRepository.findAllById(dao.getCids());
        int points = dao.getPoints();
        for (Customer customer : customers) {
            if (customer.getPoints() < points) {
                return false;
            }
        }
        for (Customer customer : customers) {
            customer.setPoints(customer.getPoints() - points);
        }
        customerRepository.saveAll(customers);
        return true;
    }

    public boolean redeemCustomerPoints(Customer customer, int points) {
        if (customer.getPoints() < points) {
            return false;
        }
        customer.setPoints(customer.getPoints() - points);
        customerRepository.save(customer);
        return true;
    }

    public Customer findById(Long cid) {
        return customerRepository.findById(cid).orElse(null);
    }
}
