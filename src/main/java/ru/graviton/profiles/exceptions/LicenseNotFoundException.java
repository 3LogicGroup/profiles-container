package ru.graviton.profiles.exceptions;

public class LicenseNotFoundException extends RuntimeException {

    public LicenseNotFoundException(String message) {
        super(message);
    }

}