package br.com.devpasso.order_management.domain.exception;

public class ResourceNotFoundException extends BaseDomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
