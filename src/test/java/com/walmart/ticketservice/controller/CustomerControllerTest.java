package com.walmart.ticketservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.ticketservice.model.dto.CustomerRequest;
import com.walmart.ticketservice.service.CustomerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CustomerService customerService;

  private JacksonTester<CustomerRequest> jsonTester;

  @Autowired private ObjectMapper objectMapper;

  @Before
  public void setup() {
    JacksonTester.initFields(this, objectMapper);
  }

  @Test
  public void createCustomer() throws Exception {
    CustomerRequest customerRequestDTO = new CustomerRequest();
    final String customerRequest = jsonTester.write(customerRequestDTO).getJson();
    given(customerService.save(any(CustomerRequest.class))).willReturn("customerId");
    this.mockMvc
        .perform(
            post("/ticketservice/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerRequest))
        .andExpect(status().isCreated());
    verify(customerService).save(any(CustomerRequest.class));
  }

}
