package com.example.bankpro.controller;

import com.example.bankpro.model.Account;
import com.example.bankpro.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService service;

    @Test
    public void list_returnsAccounts() throws Exception {
        when(service.getAll()).thenReturn(List.of(new Account("A1","Alice", new BigDecimal("100.00"))));
        mvc.perform(get("/api/accounts").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void get_existing_returns200() throws Exception {
        when(service.getById("A100")).thenReturn(Optional.of(new Account("A100","Alice", new BigDecimal("1200.50"))));
        mvc.perform(get("/api/accounts/A100").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.owner").value("Alice"));
    }
}
