package com.backandwhite.domain.model;

import static com.backandwhite.provider.UserProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserTest {

    @Test
    void getAuthorities_withRoles_returnsRoles() {
        User user = user();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).isEqualTo(user.getRoles());
    }

    @Test
    void getAuthorities_withNullRoles_returnsFallbackRoleUser() {
        User user = user().withRoles(null);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void getAuthorities_withEmptyRoles_returnsFallbackRoleUser() {
        User user = user().withRoles(Collections.emptyList());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void getUsername_returnsEmail() {
        User user = user();

        assertThat(user.getUsername()).isEqualTo(USER_EMAIL);
    }

    @Test
    void isEnabled_delegatesToField() {
        User user = user();

        assertThat(user.isEnabled()).isEqualTo(USER_ENABLED);
    }

    @Test
    void isAccountNonExpired_delegatesToField() {
        User user = user();

        assertThat(user.isAccountNonExpired()).isEqualTo(USER_ACCOUNT_NON_EXPIRED);
    }

    @Test
    void isAccountNonLocked_delegatesToField() {
        User user = user();

        assertThat(user.isAccountNonLocked()).isEqualTo(USER_ACCOUNT_NON_LOCKED);
    }

    @Test
    void isCredentialsNonExpired_delegatesToField() {
        User user = user();

        assertThat(user.isCredentialsNonExpired()).isEqualTo(USER_CREDENTIALS_NON_EXPIRED);
    }
}
