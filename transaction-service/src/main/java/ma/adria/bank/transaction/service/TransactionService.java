package ma.adria.bank.transaction.service;

import lombok.RequiredArgsConstructor;
import ma.adria.bank.transaction.client.*;
import ma.adria.bank.transaction.domain.Transaction;
import ma.adria.bank.transaction.event.TransactionEventPublisher;
import ma.adria.bank.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repo;
    private final AccountGateway accounts;
    private final TransactionEventPublisher publisher;

    @Value("${bank.transaction.max-amount:1000}")
    private BigDecimal maxAmount;

    public Transaction create(CreateTransactionCommand c) {
        if (c.amount() == null || c.amount().signum() <= 0)
            throw new BusinessException("Le montant doit être strictement positif");
        if (c.amount().compareTo(maxAmount) > 0)
            throw new BusinessException("Montant supérieur au plafond: " + maxAmount);

        AccountDto account = accounts.getAccount(c.accountId());      // existence + solde
        if (!"ACTIVE".equals(account.status()))
            throw new BusinessException("Le compte n'est pas actif");
        if (!account.currency().equalsIgnoreCase(c.currency()))
            throw new BusinessException("Devise différente de celle du compte");
        BigDecimal balance = BigDecimal.valueOf(account.balance());
        if (balance.compareTo(c.amount()) < 0)
            throw new BusinessException("Solde insuffisant");

        Transaction t = repo.save(new Transaction(null, c.accountId(), c.amount(), c.currency(),
                c.merchantCategory(), c.country(), Instant.now()));
        accounts.updateBalance(account.id(), balance.subtract(c.amount()).doubleValue());
        publisher.publish(t);                                         // après persistance réussie
        return t;
    }

    public List<Transaction> findAll() { return repo.findAll(); }
    public List<Transaction> findByAccount(UUID accountId) { return repo.findByAccountId(accountId); }
    public Transaction findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Transaction introuvable: " + id));
    }
}