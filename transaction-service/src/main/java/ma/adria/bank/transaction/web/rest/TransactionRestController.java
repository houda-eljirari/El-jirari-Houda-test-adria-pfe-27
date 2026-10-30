package ma.adria.bank.transaction.web.rest;

import lombok.RequiredArgsConstructor;
import ma.adria.bank.transaction.domain.Transaction;
import ma.adria.bank.transaction.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionRestController {
    private final TransactionService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@RequestBody CreateTransactionCommand c) { return service.create(c); }

    @GetMapping public List<Transaction> all() { return service.findAll(); }
    @GetMapping("/{id}") public Transaction one(@PathVariable UUID id) { return service.findById(id); }
    @GetMapping("/account/{accountId}")
    public List<Transaction> byAccount(@PathVariable UUID accountId) { return service.findByAccount(accountId); }
}