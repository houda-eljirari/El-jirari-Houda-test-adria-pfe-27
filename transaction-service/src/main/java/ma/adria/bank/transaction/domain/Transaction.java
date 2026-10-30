package ma.adria.bank.transaction.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Transaction {
    @Id @GeneratedValue
    private UUID id;
    private UUID accountId;
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    private String currency;
    private String merchantCategory;
    private String country;
    private Instant createdAt;
}