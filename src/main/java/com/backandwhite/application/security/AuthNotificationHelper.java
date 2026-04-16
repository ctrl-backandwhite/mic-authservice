package com.backandwhite.application.security;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthNotificationHelper {

    private static final String UNKNOWN = "Unknown";

    private final NotificationEventPort notificationEventPort;
    private final UserRepository userRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AuthNotificationHelper(NotificationEventPort notificationEventPort, UserRepository userRepository) {
        this.notificationEventPort = notificationEventPort;
        this.userRepository = userRepository;
    }

    public void sendAuthNotification(String username, String subject, String templateName) {
        sendAuthNotification(username, subject, templateName, null);
    }

    public void sendAuthNotification(String username, String subject, String templateName, HttpServletRequest request) {
        if (username == null || username.isBlank()) {
            return;
        }
        // Extract request data on the calling thread before Tomcat recycles the request
        Map<String, String> deviceInfo = (request != null) ? extractDeviceInfo(request) : Map.of();
        executor.execute(() -> {
            try {
                String normalized = username.trim().toLowerCase();
                User user = userRepository.findUserByEmail(normalized);
                if (user == null) {
                    user = userRepository.findUserByNickName(normalized);
                }
                if (user == null || user.getEmail() == null) {
                    return;
                }

                Map<String, String> variables = new HashMap<>();
                variables.put("name", user.getName());
                variables.putAll(deviceInfo);

                notificationEventPort.sendNotificationEvent(
                        new EmailNotificationRequest(user.getEmail(), subject, templateName, variables));
                log.debug("::> Auth notification [{}] sent for user", templateName);
            } catch (Exception e) {
                log.warn("::> Could not send auth notification [{}]", templateName, e);
            }
        });
    }

    private Map<String, String> extractDeviceInfo(HttpServletRequest request) {
        Map<String, String> info = new HashMap<>();
        info.put("dateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.isBlank()) {
            info.put("browser", parseBrowser(userAgent));
            info.put("operatingSystem", parseOperatingSystem(userAgent));
        }

        String ip = extractClientIp(request);
        info.put("ipAddress", ip);
        info.put("location", resolveLocation(ip));
        info.put("changePasswordUrl", resolveChangePasswordUrl(request));
        return info;
    }

    private String extractClientIp(HttpServletRequest request) {
        String[] headerNames = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private String resolveChangePasswordUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        boolean isDefaultPort = ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443);
        String baseUrl = scheme + "://" + serverName + (isDefaultPort ? "" : ":" + port);
        return baseUrl + "/forgot-password.html";
    }

    private String resolveLocation(String ip) {
        if (ip == null || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || ip.startsWith("192.168.")
                || ip.startsWith("10.") || ip.startsWith("172.")) {
            return "Local network";
        }
        return UNKNOWN;
    }

    static String parseBrowser(String userAgent) {
        if (userAgent == null) {
            return UNKNOWN;
        }
        if (userAgent.contains("Edg/")) {
            return "Microsoft Edge";
        }
        if (userAgent.contains("OPR/") || userAgent.contains("Opera")) {
            return "Opera";
        }
        if (userAgent.contains("Chrome/") && !userAgent.contains("Edg/") && !userAgent.contains("OPR/")) {
            return "Google Chrome";
        }
        if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) {
            return "Safari";
        }
        if (userAgent.contains("Firefox/")) {
            return "Mozilla Firefox";
        }
        return UNKNOWN;
    }

    static String parseOperatingSystem(String userAgent) {
        if (userAgent == null) {
            return UNKNOWN;
        }
        if (userAgent.contains("Windows NT 10")) {
            return "Windows 10/11";
        }
        if (userAgent.contains("Windows")) {
            return "Windows";
        }
        if (userAgent.contains("Mac OS X")) {
            return "macOS";
        }
        if (userAgent.contains("Android")) {
            return "Android";
        }
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        if (userAgent.contains("Linux")) {
            return "Linux";
        }
        return UNKNOWN;
    }
}
