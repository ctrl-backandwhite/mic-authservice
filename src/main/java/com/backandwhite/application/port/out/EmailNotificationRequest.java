package com.backandwhite.application.port.out;

import java.util.Map;

public record EmailNotificationRequest(String recipient, String subject, String templateName,
                Map<String, String> variables) {
}
