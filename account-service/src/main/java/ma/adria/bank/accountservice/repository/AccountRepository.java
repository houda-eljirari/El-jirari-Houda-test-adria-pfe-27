package ma.adria.bank.accountservice.repository;

import ma.adria.bank.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {}