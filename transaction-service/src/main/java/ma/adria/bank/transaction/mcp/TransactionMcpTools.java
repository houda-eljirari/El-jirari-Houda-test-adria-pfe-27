package ma.adria.bank.transaction.mcp;

import lombok.RequiredArgsConstructor;
import ma.adria.bank.transaction.domain.Transaction;
import ma.adria.bank.transaction.service.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionMcpTools {
    private final TransactionService service;

    @Tool(description = "Enregistrer une transaction (débit) sur un compte bancaire")
    public Transaction createTransaction(
            @ToolParam(description = "UUID du compte") String accountId,
            @ToolParam(description = "Montant strictement positif") double amount,
            @ToolParam(description = "Devise, ex: EUR") String currency,
            @ToolParam(description = "Catégorie du marchand") String merchantCategory,
            @ToolParam(description = "Pays, ex: MA") String country) {
        return service.create(new CreateTransactionCommand(UUID.fromString(accountId),
                BigDecimal.valueOf(amount), currency, merchantCategory, country));
    }

    @Tool(description = "Lister les transactions d'un compte")
    public List<Transaction> listTransactions(@ToolParam(description = "UUID du compte") String accountId) {
        return service.findByAccount(UUID.fromString(accountId));
    }
}