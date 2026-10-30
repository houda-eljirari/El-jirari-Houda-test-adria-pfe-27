package ma.adria.bank.accountservice.service;

import lombok.RequiredArgsConstructor;
import ma.adria.bank.account.domain.*;
import ma.adria.bank.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repo;

    public Account create(Account a) {
        if (a.getStatus() == null) a.setStatus(AccountStatus.ACTIVE);
        return repo.save(a);
    }

    public List<Account> findAll() { return repo.findAll(); }

    public Account findById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compte introuvable: " + id));
    }

    public Account updateBalance(UUID id, double newBalance) {
        Account a = findById(id);
        a.setBalance(newBalance);
        return repo.save(a);
    }

    @Transactional
    public void transfer(UUID fromId, UUID toId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Le montant doit être positif");
        if (fromId.equals(toId)) throw new IllegalArgumentException("Comptes identiques");
        Account from = findById(fromId);
        Account to = findById(toId);
        if (from.getStatus() != AccountStatus.ACTIVE || to.getStatus() != AccountStatus.ACTIVE)
            throw new IllegalStateException("Les deux comptes doivent être actifs");
        if (!from.getCurrency().equals(to.getCurrency()))
            throw new IllegalStateException("Devises différentes");
        if (from.getBalance() < amount) throw new IllegalStateException("Solde insuffisant");
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
    }
}