package com.example.bankpro.integration;

import com.example.bankpro.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountIntegrationTest {
    @Autowired
    private TestRestTemplate rest;

    @Test
    public void createAndGetAccount() {
        Account newAcc = new Account(null, "Charlie", new BigDecimal("55.75"));
        ResponseEntity<Account> created = rest.postForEntity("/api/accounts", newAcc, Account.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Account body = created.getBody();
        assertThat(body).isNotNull();
        String id = body.getId();
        ResponseEntity<Account> fetched = rest.getForEntity("/api/accounts/" + id, Account.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody().getOwner()).isEqualTo("Charlie");
    }
}
