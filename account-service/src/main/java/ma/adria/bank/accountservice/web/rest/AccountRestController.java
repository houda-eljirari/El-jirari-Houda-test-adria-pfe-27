package ma.adria.bank.accountservice.web.rest;

import lombok.RequiredArgsConstructor;
import ma.adria.bank.account.domain.Account;
import ma.adria.bank.account.service.AccountService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {
    private final AccountService service;

    public record TransferRequest(UUID fromId, UUID toId, double amount) {}

    @GetMapping public List<Account> all() { return service.findAll(); }
    @GetMapping("/{id}") public Account one(@PathVariable UUID id) { return service.findById(id); }
    @PostMapping public Account create(@RequestBody Account a) { return service.create(a); }

    @PutMapping("/{id}/balance")
    public Account balance(@PathVariable UUID id, @RequestParam double value) {
        return service.updateBalance(id, value);
    }

    @PostMapping("/transfer")
    public Map<String, String> transfer(@RequestBody TransferRequest r) {
        service.transfer(r.fromId(), r.toId(), r.amount());
        return Map.of("status", "OK");
    }
}