package com.walmart.ticketservice.controller;

import com.walmart.ticketservice.model.Customer;
import com.walmart.ticketservice.model.dto.CustomerRequest;
import com.walmart.ticketservice.model.dto.CustomerResponseDto;
import com.walmart.ticketservice.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/ticketservice")
@Api(value = "RegisterCustomer")
public class CustomerController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

  private CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping(value = "/customer")
  @ApiOperation(
    value = "Request to create a costumer",
    notes =
        "We need to create a customer before we can purchase  or reserve any seat . Returns the Venue ID."
  )
  @ApiResponses({@ApiResponse(code = 201, message = "Customer creation request successful")})
  public ResponseEntity<String> registerCustomer(@NotNull @RequestBody CustomerRequest customer) {
    String customerID = customerService.save(customer);
    return new ResponseEntity<>(customerID, HttpStatus.CREATED);
  }

  @PutMapping(value = "/customer")
  @ApiOperation(
    value = "Request to update  a  costumer",
    notes =
        "We need to create a customer before we can purchase  or reserve any seat . Returns the Venue ID."
  )
  @ApiResponses({@ApiResponse(code = 201, message = "Customer creation request successful")})
  public ResponseEntity<String> updateCustomer(@NotNull @RequestBody Customer customer) {
    String customerID = customerService.update(customer);
    return new ResponseEntity<>(customerID, HttpStatus.OK);
  }

  @DeleteMapping("/customer/{customerId}")
  @ApiResponses({
    @ApiResponse(
      code = 204,
      message = "Delete the Show given the show id. We cannot update the id."
    ),
    @ApiResponse(code = 404, message = "Not available customer")
  })
  public ResponseEntity deleteShow(@PathVariable String customerId) {
    if (StringUtils.isBlank(customerId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    String deletedCustomer = customerService.delete(customerId);

    LOGGER.info("Deleted customer=" + deletedCustomer);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/customer/{customerId}")
  @ApiResponses({
    @ApiResponse(code = 204, message = "Get customer information given the show id."),
    @ApiResponse(code = 404, message = "Not available shows for the venue")
  })
  public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable String customerId) {
    if (StringUtils.isBlank(customerId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    Optional<CustomerResponseDto> customer = customerService.getCustomer(customerId);
    if (!customer.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(customer.get(), HttpStatus.OK);
  }
}
