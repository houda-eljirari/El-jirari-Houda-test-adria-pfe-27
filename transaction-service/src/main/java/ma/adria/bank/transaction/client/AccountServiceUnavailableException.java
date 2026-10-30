package ma.adria.bank.transaction.client;

public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(String msg, Throwable cause) { super(msg, cause); }
}