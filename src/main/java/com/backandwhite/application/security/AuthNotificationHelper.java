package com.backandwhite.application.security;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class AuthNotificationHelper {

    private final NotificationEventPort notificationEventPort;
    private final UserRepository userRepository;

    public void sendAuthNotification(String username, String subject, String templateName) {
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
                    .setSubject(subject).setTemplateName(templateName).setVariables(variables).build();

            notificationEventPort.sendNotificationEvent(event);
            log.debug("::> Auth notification [{}] sent for user", templateName);
        } catch (Exception e) {
            log.warn("::> Could not send auth notification [{}]", templateName, e);
        }
    }
}
