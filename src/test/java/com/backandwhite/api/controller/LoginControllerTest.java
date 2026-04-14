package com.backandwhite.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void login_forwardsToLoginHtml() {
        LoginController controller = new LoginController();

        String view = controller.login();

        assertThat(view).isEqualTo("forward:/login.html");
    }
}
