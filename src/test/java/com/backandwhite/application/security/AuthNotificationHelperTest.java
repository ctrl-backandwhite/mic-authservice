package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthNotificationHelperTest {

    @Mock
    private NotificationEventPort notificationEventPort;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthNotificationHelper helper;

    @Test
    void sendAuthNotification_withValidUser_sendsNotification() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert");

        await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> verify(notificationEventPort).sendNotificationEvent(any(EmailNotificationRequest.class)));
    }

    @Test
    void sendAuthNotification_withNullUsername_doesNotSend() {
        helper.sendAuthNotification(null, "Login Alert", "login-alert");

        verify(userRepository, never()).findUserByEmail(any());
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_withBlankUsername_doesNotSend() {
        helper.sendAuthNotification("   ", "Login Alert", "login-alert");

        verify(userRepository, never()).findUserByEmail(any());
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_whenUserNotFound_doesNotSend() {
        when(userRepository.findUserByEmail("unknown@test.com")).thenReturn(null);

        helper.sendAuthNotification("unknown@test.com", "Login Alert", "login-alert");

        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(userRepository).findUserByEmail("unknown@test.com"));
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_whenUserHasNullEmail_doesNotSend() {
        User user = User.builder().name("Ana").email(null).build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert");

        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(userRepository).findUserByEmail("ana@test.com"));
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_whenExceptionThrown_handlesGracefully() {
        when(userRepository.findUserByEmail("ana@test.com")).thenThrow(new RuntimeException("DB error"));

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert");

        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(userRepository).findUserByEmail("ana@test.com"));
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_trimsAndLowercasesUsername() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        helper.sendAuthNotification("  ANA@TEST.COM  ", "Login Alert", "login-alert");

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            verify(userRepository).findUserByEmail("ana@test.com");
            verify(notificationEventPort).sendNotificationEvent(any(EmailNotificationRequest.class));
        });
    }

    @Test
    void sendAuthNotification_withRequest_enrichesDeviceInfo() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36");
        request.setRemoteAddr("203.0.113.50");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        EmailNotificationRequest captured = captor.getValue();
        assertThat(captured.variables()).containsKey("dateTime");
        assertThat(captured.variables()).containsEntry("browser", "Google Chrome");
        assertThat(captured.variables()).containsEntry("operatingSystem", "Windows 10/11");
        assertThat(captured.variables()).containsEntry("ipAddress", "203.0.113.50");
        assertThat(captured.variables()).containsEntry("lang", "es");
    }

    @Test
    void sendAuthNotification_withRequest_extractsIpFromXForwardedFor() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "198.51.100.10, 10.0.0.1");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("ipAddress", "198.51.100.10");
    }

    @Test
    void sendAuthNotification_withRequest_localIpResolvesToLocalNetwork() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("location", "Local network");
    }

    @Test
    void sendAuthNotification_withRequest_publicIpResolvesToUnknown() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("203.0.113.50");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("location", "Unknown");
    }

    @Test
    void sendAuthNotification_withRequest_includesChangePasswordUrl() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("auth.example.com");
        request.setServerPort(443);
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("changePasswordUrl",
                "https://auth.example.com/forgot-password.html");
    }

    @Test
    void sendAuthNotification_withRequest_nonDefaultPortIncludedInUrl() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("changePasswordUrl",
                "http://localhost:8080/forgot-password.html");
    }

    @Test
    void sendAuthNotification_withRequest_xRealIpHeader() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", "198.51.100.20");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("ipAddress", "198.51.100.20");
    }

    @Test
    void sendAuthNotification_withRequest_loopbackIpIsLocalNetwork() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("location", "Local network");
    }

    @Test
    void sendAuthNotification_withRequest_ipv6LoopbackIsLocalNetwork() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("0:0:0:0:0:0:0:1");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("location", "Local network");
    }

    @Test
    void sendAuthNotification_withRequest_tenNetworkIsLocalNetwork() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.5");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("location", "Local network");
    }

    @Test
    void sendAuthNotification_withRequest_172NetworkIsLocalNetwork() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("172.16.0.1");
        request.addHeader("User-Agent", "TestAgent");

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert", request);

        ArgumentCaptor<EmailNotificationRequest> captor = ArgumentCaptor.forClass(EmailNotificationRequest.class);
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(notificationEventPort).sendNotificationEvent(captor.capture()));

        assertThat(captor.getValue().variables()).containsEntry("location", "Local network");
    }

    @Nested
    @DisplayName("parseBrowser")
    class ParseBrowserTests {

        @Test
        void null_returnsUnknown() {
            assertThat(AuthNotificationHelper.parseBrowser(null)).isEqualTo("Unknown");
        }

        @Test
        void edge() {
            assertThat(AuthNotificationHelper.parseBrowser("Mozilla/5.0 Edg/120.0")).isEqualTo("Microsoft Edge");
        }

        @Test
        void opera() {
            assertThat(AuthNotificationHelper.parseBrowser("Mozilla/5.0 OPR/100.0")).isEqualTo("Opera");
        }

        @Test
        void operaLegacy() {
            assertThat(AuthNotificationHelper.parseBrowser("Opera/9.80")).isEqualTo("Opera");
        }

        @Test
        void chrome() {
            assertThat(AuthNotificationHelper
                    .parseBrowser("Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36"))
                    .isEqualTo("Google Chrome");
        }

        @Test
        void safari() {
            assertThat(AuthNotificationHelper.parseBrowser("Mozilla/5.0 (Macintosh) AppleWebKit/605.1.15 Safari/605.1"))
                    .isEqualTo("Safari");
        }

        @Test
        void firefox() {
            assertThat(AuthNotificationHelper.parseBrowser("Mozilla/5.0 (X11; Linux) Gecko/20100101 Firefox/120.0"))
                    .isEqualTo("Mozilla Firefox");
        }

        @Test
        void unknownAgent() {
            assertThat(AuthNotificationHelper.parseBrowser("curl/7.68.0")).isEqualTo("Unknown");
        }
    }

    @Nested
    @DisplayName("parseOperatingSystem")
    class ParseOperatingSystemTests {

        @Test
        void null_returnsUnknown() {
            assertThat(AuthNotificationHelper.parseOperatingSystem(null)).isEqualTo("Unknown");
        }

        @Test
        void windows10() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("Mozilla/5.0 (Windows NT 10.0; Win64; x64)"))
                    .isEqualTo("Windows 10/11");
        }

        @Test
        void windowsOther() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("Mozilla/5.0 (Windows NT 6.1)"))
                    .isEqualTo("Windows");
        }

        @Test
        void macOS() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"))
                    .isEqualTo("macOS");
        }

        @Test
        void android() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("Mozilla/5.0 (Linux; Android 13)"))
                    .isEqualTo("Android");
        }

        @Test
        void iphone() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("Mozilla/5.0 (iPhone; CPU iPhone OS 17_0)"))
                    .isEqualTo("iOS");
        }

        @Test
        void ipad() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("Mozilla/5.0 (iPad; CPU OS 17_0)")).isEqualTo("iOS");
        }

        @Test
        void linux() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("Mozilla/5.0 (X11; Linux x86_64)"))
                    .isEqualTo("Linux");
        }

        @Test
        void unknownOS() {
            assertThat(AuthNotificationHelper.parseOperatingSystem("curl/7.68.0")).isEqualTo("Unknown");
        }
    }
}
