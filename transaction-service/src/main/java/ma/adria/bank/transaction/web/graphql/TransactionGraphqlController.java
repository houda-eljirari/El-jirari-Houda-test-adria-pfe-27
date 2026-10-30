package ma.adria.bank.transaction.web.graphql;

import lombok.RequiredArgsConstructor;
import ma.adria.bank.transaction.domain.Transaction;
import ma.adria.bank.transaction.service.*;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class TransactionGraphqlController {
    private final TransactionService service;

    @QueryMapping public List<Transaction> transactions() { return service.findAll(); }

    @QueryMapping
    public List<Transaction> transactionsByAccount(@Argument UUID accountId) {
        return service.findByAccount(accountId);
    }

    @MutationMapping
    public Transaction createTransaction(@Argument CreateTransactionCommand input) {
        return service.create(input);
    }
}