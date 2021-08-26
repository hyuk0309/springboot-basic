package org.programmers.kdt.customer.service;

import org.programmers.kdt.customer.Customer;
import org.programmers.kdt.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer signUp(UUID customerId, String name, String email) throws IOException {
        Customer customer = new Customer(customerId, name, email, LocalDateTime.now(), LocalDateTime.now());
        return customerRepository.insert(customer);
    }

    @Override
    public Optional<Customer> findCustomerById(UUID customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public List<Customer> findCustomersByName(String name) {
        return customerRepository.findByName(name);
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.fineByEmail(email);
    }

    @Override
    public Customer registerToBlacklist(Customer customer) throws IOException {
        return customerRepository.registerToBlacklist(customer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> findAllBlacklistCustomer() {
        return customerRepository.findAllBlacklistCustomer();
    }

    @Override
    public Boolean isBlacklisted(Customer customer) {
        return customerRepository.findAllBlacklistCustomer().contains(customer);
    }
}