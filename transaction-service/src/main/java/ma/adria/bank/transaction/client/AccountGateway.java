package ma.adria.bank.transaction.client;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountGateway {
    private final AccountClient client;

    @CircuitBreaker(name = "accountService", fallbackMethod = "getFallback")
    @Retry(name = "accountService")
    public AccountDto getAccount(UUID id) { return client.getAccount(id); }

    @CircuitBreaker(name = "accountService", fallbackMethod = "updateFallback")
    @Retry(name = "accountService")
    public AccountDto updateBalance(UUID id, double value) { return client.updateBalance(id, value); }

    AccountDto getFallback(UUID id, FeignException.NotFound e) {
        throw new NoSuchElementException("Compte introuvable: " + id);
    }
    AccountDto getFallback(UUID id, Throwable t) {
        throw new AccountServiceUnavailableException("account-service indisponible", t);
    }
    AccountDto updateFallback(UUID id, double value, Throwable t) {
        throw new AccountServiceUnavailableException("Mise à jour du solde impossible", t);
    }
}
