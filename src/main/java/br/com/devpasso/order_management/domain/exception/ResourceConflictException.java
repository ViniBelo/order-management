package br.com.devpasso.order_management.domain.exception;

public class ResourceConflictException extends BaseDomainException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
