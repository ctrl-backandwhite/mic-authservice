package com.backandwhite.application.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.CustomerRegisteredRequest;
import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;

@ExtendWith(MockitoExtension.class)
class GoogleOAuth2SuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private NotificationEventPort notificationEventPort;

    @Mock
    private AuthEventPort authEventPort;

    private GoogleOAuth2SuccessHandler buildHandler() {
        GoogleOAuth2SuccessHandler handler = new GoogleOAuth2SuccessHandler(userRepository, roleRepository,
                notificationEventPort, authEventPort);
        handler.setRedirectStrategy(mock(RedirectStrategy.class));
        return handler;
    }

    private Authentication oAuth2Authentication(String email, String givenName, String familyName) {
        Map<String, Object> attributes = new java.util.HashMap<>();
        attributes.put("sub", "google-id-123");
        if (email != null) {
            attributes.put("email", email);
        }
        if (givenName != null) {
            attributes.put("given_name", givenName);
        }
        if (familyName != null) {
            attributes.put("family_name", familyName);
        }

        OAuth2User oAuth2User = new DefaultOAuth2User(List.of(() -> "ROLE_USER"), attributes, "sub");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(oAuth2User);
        return auth;
    }

    @Test
    void onAuthenticationSuccess_newUser_createsUserAndSendsNotifications() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("ana@gmail.com")).thenReturn(null);
        Role guestRole = Role.builder().id(1L).uniqueName("ROLE_GUEST").name("Guest").build();
        when(roleRepository.findAll()).thenReturn(List.of(guestRole));
        User savedUser = User.builder().id(10L).email("ana@gmail.com").name("Ana").lastName("Lopez").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("ana@gmail.com", "Ana", "Lopez"));

        verify(userRepository).save(any(User.class));
        verify(notificationEventPort).sendNotificationEvent(any(EmailNotificationRequest.class));
        verify(authEventPort).publishCustomerRegistered(any(CustomerRegisteredRequest.class));
    }

    @Test
    void onAuthenticationSuccess_existingUser_doesNotCreateUser() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        User existing = User.builder().id(5L).email("existing@gmail.com").build();
        when(userRepository.findUserByEmail("existing@gmail.com")).thenReturn(existing);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response,
                oAuth2Authentication("existing@gmail.com", "Existing", "User"));

        verify(userRepository, never()).save(any(User.class));
        verify(notificationEventPort, never()).sendNotificationEvent(any());
        verify(authEventPort, never()).publishCustomerRegistered(any());
    }

    @Test
    void onAuthenticationSuccess_nullEmail_redirectsToError() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication(null, "Ana", "Lopez"));

        verify(userRepository, never()).findUserByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void onAuthenticationSuccess_blankEmail_redirectsToError() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("   ", "Ana", "Lopez"));

        verify(userRepository, never()).findUserByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void onAuthenticationSuccess_repositoryThrows_handlesGracefully() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("error@gmail.com")).thenThrow(new RuntimeException("DB error"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("error@gmail.com", "Ana", "Lopez"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void onAuthenticationSuccess_newUser_roleRepoThrows_stillCreatesUser() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("ana@gmail.com")).thenReturn(null);
        when(roleRepository.findAll()).thenThrow(new RuntimeException("ROLE table down"));
        User savedUser = User.builder().id(11L).email("ana@gmail.com").name("Ana").lastName("Lopez").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("ana@gmail.com", "Ana", "Lopez"));

        // User was still saved even though role lookup blew up.
        verify(userRepository).save(any(User.class));
        verify(authEventPort).publishCustomerRegistered(any(CustomerRegisteredRequest.class));
    }

    @Test
    void onAuthenticationSuccess_newUser_welcomeEmailFails_doesNotPropagate() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("ana@gmail.com")).thenReturn(null);
        Role guestRole = Role.builder().id(1L).uniqueName("ROLE_GUEST").name("Guest").build();
        when(roleRepository.findAll()).thenReturn(List.of(guestRole));
        User savedUser = User.builder().id(12L).email("ana@gmail.com").name("Ana").lastName("Lopez").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doThrow(new RuntimeException("SMTP failure")).when(notificationEventPort)
                .sendNotificationEvent(any(EmailNotificationRequest.class));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("ana@gmail.com", "Ana", "Lopez"));

        // Customer-registered event still emitted despite the email failure.
        verify(authEventPort).publishCustomerRegistered(any(CustomerRegisteredRequest.class));
    }

    @Test
    void onAuthenticationSuccess_newUser_acceptLanguageEnglish_setsEnLang() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("ana@gmail.com")).thenReturn(null);
        Role guestRole = Role.builder().id(1L).uniqueName("ROLE_GUEST").build();
        when(roleRepository.findAll()).thenReturn(List.of(guestRole));
        User savedUser = User.builder().id(13L).email("ana@gmail.com").name("Ana").lastName("Lopez").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "en-US,en;q=0.9");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("ana@gmail.com", "Ana", "Lopez"));

        org.mockito.ArgumentCaptor<EmailNotificationRequest> captor = org.mockito.ArgumentCaptor
                .forClass(EmailNotificationRequest.class);
        verify(notificationEventPort).sendNotificationEvent(captor.capture());
        org.assertj.core.api.Assertions.assertThat(captor.getValue().variables()).containsEntry("lang", "en");
    }

    @ParameterizedTest(name = "Accept-Language={0} -> lang={1}")
    @CsvSource({"'pt-BR,pt;q=0.9',pt", "'es-ES,es;q=0.9',es", "'   ',es", "ja-JP,es"})
    void onAuthenticationSuccess_newUser_acceptLanguageMapsToLang(String acceptLanguage, String expectedLang)
            throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("ana@gmail.com")).thenReturn(null);
        when(roleRepository.findAll()).thenReturn(List.of());
        User savedUser = User.builder().id(14L).email("ana@gmail.com").name("Ana").lastName("Lopez").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", acceptLanguage);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("ana@gmail.com", "Ana", "Lopez"));

        org.mockito.ArgumentCaptor<EmailNotificationRequest> captor = org.mockito.ArgumentCaptor
                .forClass(EmailNotificationRequest.class);
        verify(notificationEventPort).sendNotificationEvent(captor.capture());
        org.assertj.core.api.Assertions.assertThat(captor.getValue().variables()).containsEntry("lang", expectedLang);
    }

    @Test
    void onAuthenticationSuccess_newUser_nullGivenName_usesDefaults() throws Exception {
        GoogleOAuth2SuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("nameless@gmail.com")).thenReturn(null);
        when(roleRepository.findAll()).thenReturn(List.of());
        User savedUser = User.builder().id(18L).email("nameless@gmail.com").name("Google").lastName("User").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oAuth2Authentication("nameless@gmail.com", null, null));

        org.mockito.ArgumentCaptor<User> captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User created = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(created.getName()).isEqualTo("Google");
        org.assertj.core.api.Assertions.assertThat(created.getLastName()).isEqualTo("User");
    }
}
