package com.backandwhite.application.security;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Log4j2
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final NotificationEventPort notificationEventPort;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String redirectUrl = "/login?error";

        if (exception instanceof DisabledException) {
            redirectUrl = "/login?error=disabled";
        }

        String username = request.getParameter("username");
        sendFailedLoginNotification(username);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private void sendFailedLoginNotification(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        try {
            User user = userRepository.findUserByEmail(username.trim().toLowerCase());
            if (user == null || user.getEmail() == null) {
                return;
            }

            Map<String, String> variables = new HashMap<>();
            variables.put("name", user.getName());

            EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient(user.getEmail())
                    .setSubject("Failed login attempt detected").setTemplateName("login-failed-attempt")
                    .setVariables(variables).build();

            notificationEventPort.sendNotificationEvent(event);
            log.debug("::> Failed login notification sent for user");
        } catch (Exception e) {
            log.warn("::> Could not send failed login notification", e);
        }
    }
}
