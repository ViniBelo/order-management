package br.com.devpasso.order_management.domain.exception;

public abstract class BaseDomainException extends RuntimeException {
    protected BaseDomainException(String message) {
        super(message);
    }
    protected BaseDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
