package ma.adria.bank.transaction.service;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}