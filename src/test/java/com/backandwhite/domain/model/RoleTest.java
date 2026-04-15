package com.backandwhite.domain.model;

import static com.backandwhite.provider.RoleProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void getAuthority_returnsUniqueName() {
        Role role = adminRole();

        assertThat(role.getAuthority()).isEqualTo(ADMIN_UNIQUE_NAME);
    }
}
