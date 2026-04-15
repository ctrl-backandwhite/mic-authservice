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
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Log4j2
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final NotificationEventPort notificationEventPort;
    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(NotificationEventPort notificationEventPort,
            UserRepository userRepository) {
        this.notificationEventPort = notificationEventPort;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        sendLoginSuccessNotification(authentication.getName());
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void sendLoginSuccessNotification(String username) {
        try {
            User user = userRepository.findUserByEmail(username.trim().toLowerCase());
            if (user == null || user.getEmail() == null) {
                return;
            }

            Map<String, String> variables = new HashMap<>();
            variables.put("name", user.getName());

            EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient(user.getEmail())
                    .setSubject("New login to your account").setTemplateName("login-success")
                    .setVariables(variables).build();

            notificationEventPort.sendNotificationEvent(event);
            log.debug("::> Login success notification sent for user");
        } catch (Exception e) {
            log.warn("::> Could not send login success notification", e);
        }
    }
}
