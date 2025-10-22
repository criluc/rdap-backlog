package it.nic.rdap.service;

public class RdapNotFoundException extends RuntimeException {
    public RdapNotFoundException(String message) {
        super(message);
    }
}
