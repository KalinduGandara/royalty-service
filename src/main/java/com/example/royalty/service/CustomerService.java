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
        if (customerRepository.findByTpNumber(customer.getTpNumber()) == null) {
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
            if (row[0].isEmpty() || row[1].isEmpty() || row[2].isEmpty() || row[3].isEmpty() || row[14].isEmpty()) {
                incompleteRows.add(row);
                continue;
            }
            Customer customer = customerRepository.findByTpNumber(row[0]);
            if (customer != null) {
                customer.setPoints(customer.getPoints() + Integer.parseInt(row[14]));

            } else {
                customer = new Customer();
                customer.setPoints(Integer.parseInt(row[14]));
                customer.setTpNumber(row[0]);
            }
            customer.setName(row[1]);
            customer.setAddress(row[2]);
            customer.setPhone(row[3]);
            customer.setCity(row[4]);
            customer.setDistrict(row[5]);
            customer.setProvince(row[6]);
            customer.setSalesPersonTerritory(row[7]);
            customer.setRegion(row[8]);
            customer.setAssignedCMDE(row[9]);
            customer.setLoyaltyStatus(row[10]);
            customer.setCurrentAveConsumptionPM(row[11]);
            customer.setLinkedDealer1(row[12]);
            customer.setLinkedDealer2(row[13]);
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
        customer1.setAddress(customer.getAddress());
        customer1.setCity(customer.getCity());
        customer1.setDistrict(customer.getDistrict());
        customer1.setLoyaltyStatus(customer.getLoyaltyStatus());
        customer1.setName(customer.getName());
        customer1.setPhone(customer.getPhone());
        customer1.setPoints(customer.getPoints());
        customer1.setProvince(customer.getProvince());
        customer1.setRegion(customer.getRegion());
        customer1.setSalesPersonTerritory(customer.getSalesPersonTerritory());
        customer1.setTpNumber(customer.getTpNumber());
        customer1.setAssignedCMDE(customer.getAssignedCMDE());
        customer1.setCurrentAveConsumptionPM(customer.getCurrentAveConsumptionPM());
        customer1.setLinkedDealer1(customer.getLinkedDealer1());
        customer1.setLinkedDealer2(customer.getLinkedDealer2());

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
