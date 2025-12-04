package com.example.bankpro.service;

import com.example.bankpro.model.Account;
import com.example.bankpro.repository.InMemoryAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final InMemoryAccountRepository repo;

    public AccountService(InMemoryAccountRepository repo) {
        this.repo = repo;
    }

    public List<Account> getAll() {
        return repo.findAll();
    }

    public Optional<Account> getById(String id) {
        return repo.findById(id);
    }

    public Account create(Account account) {
        return repo.save(account);
    }

    public Optional<Account> update(String id, Account update) {
        return repo.findById(id).map(existing -> {
            existing.setOwner(update.getOwner() != null ? update.getOwner() : existing.getOwner());
            existing.setBalance(update.getBalance() != null ? update.getBalance() : existing.getBalance());
            repo.save(existing);
            return existing;
        });
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
