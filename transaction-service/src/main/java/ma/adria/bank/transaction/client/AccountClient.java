package ma.adria.bank.transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountClient {
    @GetMapping("/api/accounts/{id}")
    AccountDto getAccount(@PathVariable("id") UUID id);

    @PutMapping("/api/accounts/{id}/balance")
    AccountDto updateBalance(@PathVariable("id") UUID id, @RequestParam("value") double value);
}