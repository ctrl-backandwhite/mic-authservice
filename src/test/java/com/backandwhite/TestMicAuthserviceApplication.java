package com.backandwhite;

import com.backandwhite.config.TestContainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestMicAuthserviceApplication {

    public static void main(String[] args) {
        SpringApplication.from(TestMicAuthserviceApplication::main).with(TestContainersConfiguration.class).run(args);
    }
}
