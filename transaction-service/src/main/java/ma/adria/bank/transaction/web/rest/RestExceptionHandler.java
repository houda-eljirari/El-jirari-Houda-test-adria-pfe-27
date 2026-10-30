package ma.adria.bank.transaction.web.rest;

import ma.adria.bank.transaction.client.AccountServiceUnavailableException;
import ma.adria.bank.transaction.service.BusinessException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<Map<String, String>> business(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, String>> notFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(AccountServiceUnavailableException.class)
    ResponseEntity<Map<String, String>> unavailable(Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", e.getMessage()));
    }
}