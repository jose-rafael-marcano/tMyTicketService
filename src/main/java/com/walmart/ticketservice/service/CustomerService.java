package com.walmart.ticketservice.service;

import com.walmart.ticketservice.model.Address;
import com.walmart.ticketservice.model.Customer;
import com.walmart.ticketservice.model.dto.AddressDTO;
import com.walmart.ticketservice.model.dto.CustomerRequest;
import com.walmart.ticketservice.model.dto.CustomerResponseDto;
import com.walmart.ticketservice.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CustomerService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

  private CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Transactional
  public String save(CustomerRequest customer) {
    Customer customerMerged = mapCustomerRequestToCustomerEntity(customer);
    Customer response = customerRepository.save(customerMerged);
    if (response == null)
      throw new RuntimeException("Failed to save customer " + customer.getName());
    return customerMerged.getId();
  }

  /**
   * @param customer
   * @return
   */
  private Customer mapCustomerRequestToCustomerEntity(CustomerRequest customer) {
    Customer customerMerged = new Customer();
    customerMerged.setId(customer.getId());
    Address address = new Address();
    // address.setVenue(customer.getAddress().g);
    address.setCity(customer.getAddress().getCity());
    address.setId(customer.getAddress().getId());
    address.setState(customer.getAddress().getState());
    address.setStreet(customer.getAddress().getStreet());
    address.setZipCode(customer.getAddress().getZipCode());
    customerMerged.setAddress(address);
    customerMerged.setDateOfBirth(customer.getDateOfBirth());
    customerMerged.setLastFourOfCard(customer.getLastFourOfCard());
    customerMerged.setName(customer.getName());
    customerMerged.setEmail(customer.getEmail());
    return customerMerged;
  }

  @Transactional
  public String update(Customer customer) {
    Customer response = customerRepository.save(customer);
    if (response == null)
      throw new RuntimeException("Failed to save customer " + customer.getName());
    LOGGER.info("updated customer=" + customer.getId());
    return customer.getId();
  }

  @Transactional
  public String delete(String customerId) {
    customerRepository.deleteById(customerId);
    LOGGER.info("Deleted customer:{}", customerId);
    return customerId;
  }

  @Transactional
  public Optional<CustomerResponseDto> getCustomer(String customerId) {
    Optional<Customer> responses = customerRepository.findById(customerId);

    if (!responses.isPresent()) return Optional.empty();

    CustomerResponseDto customerResponseDto = new CustomerResponseDto();
    customerResponseDto.setDateOfBirth(responses.get().getDateOfBirth());
    customerResponseDto.setId(responses.get().getId());
    customerResponseDto.setLastFourOfCard(responses.get().getLastFourOfCard());
    customerResponseDto.setName(responses.get().getName());
    customerResponseDto.setEmail(responses.get().getEmail());
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setZipCode(responses.get().getAddress().getZipCode());
    addressDTO.setStreet(responses.get().getAddress().getStreet());
    addressDTO.setState(responses.get().getAddress().getState());
    addressDTO.setId(responses.get().getAddress().getId());
    addressDTO.setCity(responses.get().getAddress().getCity());

    customerResponseDto.setAddress(addressDTO);

    return Optional.ofNullable(customerResponseDto);
  }
}
