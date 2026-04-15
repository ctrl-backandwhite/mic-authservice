package com.backandwhite.application.port.out;

/**
 * Port interface for publishing authentication-related domain events.
 */
public interface AuthEventPort {

    /**
     * Publishes a customer.registered event after user registration.
     */
    void publishCustomerRegistered(CustomerRegisteredRequest request);
}
