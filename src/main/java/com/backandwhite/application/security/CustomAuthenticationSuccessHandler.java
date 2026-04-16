package com.backandwhite.application.security;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AuthNotificationHelper notificationHelper;

    public CustomAuthenticationSuccessHandler(NotificationEventPort notificationEventPort,
            UserRepository userRepository) {
        this.notificationHelper = new AuthNotificationHelper(notificationEventPort, userRepository);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        notificationHelper.sendAuthNotification(authentication.getName(), "New login to your account", "login-success",
                request);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
