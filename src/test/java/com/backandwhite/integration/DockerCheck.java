package com.backandwhite.integration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class DockerCheck {

    public static void main(String[] args) {
        try (GenericContainer<?> container =
                     new GenericContainer<>(DockerImageName.parse("alpine:latest"))
                             .withCommand("echo", "hello from docker")) {
            container.start();
            System.out.println("✅ Docker is working! Logs: " + container.getLogs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
