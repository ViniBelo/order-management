package br.com.devpasso.order_management.domain.exception;

public class InvalidOrderStateException extends BaseDomainException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
