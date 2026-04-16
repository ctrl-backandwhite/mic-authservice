package com.backandwhite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class TestMicAuthserviceApplicationTest {

    @Test
    void applicationClassExists() {
        assertThat(MicAuthserviceApplication.class).isNotNull();
    }

    @Test
    void main_callsSpringApplicationRun() {
        try (MockedStatic<SpringApplication> springApp = mockStatic(SpringApplication.class)) {
            MicAuthserviceApplication.main(new String[]{});

            springApp.verify(() -> SpringApplication.run(MicAuthserviceApplication.class, new String[]{}));
        }
    }
}
