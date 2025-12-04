package com.example.bankpro.repository;

import com.example.bankpro.model.Account;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAccountRepository {
    private final Map<String, Account> store = new ConcurrentHashMap<>();

    public InMemoryAccountRepository() {
        // seed with two accounts
        store.put("A100", new Account("A100", "Alice", new BigDecimal("1200.50")));
        store.put("A200", new Account("A200", "Bob", new BigDecimal("380.00")));
    }

    public List<Account> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Account save(Account account) {
        if (account.getId() == null || account.getId().isEmpty()) {
            account.setId(UUID.randomUUID().toString());
        }
        store.put(account.getId(), account);
        return account;
    }

    public void deleteById(String id) {
        store.remove(id);
    }

    public void clear() {
        store.clear();
    }
}
