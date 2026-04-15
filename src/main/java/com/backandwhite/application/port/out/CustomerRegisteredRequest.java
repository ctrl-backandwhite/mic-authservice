package com.backandwhite.application.port.out;

public record CustomerRegisteredRequest(String userId, String email, String firstName, String lastName) {
}
