package com.backandwhite.application.security;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Log4j2
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuthNotificationHelper notificationHelper;

    public CustomAuthenticationFailureHandler(NotificationEventPort notificationEventPort,
            UserRepository userRepository) {
        this.notificationHelper = new AuthNotificationHelper(notificationEventPort, userRepository);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String redirectUrl = "/login?error";

        if (exception instanceof DisabledException) {
            redirectUrl = "/login?error=disabled";
        }

        String username = request.getParameter("username");
        notificationHelper.sendAuthNotification(username, "Failed login attempt detected", "login-failed-attempt",
                request);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
