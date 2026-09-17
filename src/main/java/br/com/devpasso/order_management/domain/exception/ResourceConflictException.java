package br.com.devpasso.order_management.domain.exception;

public class ResourceConflictException extends DomainException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
