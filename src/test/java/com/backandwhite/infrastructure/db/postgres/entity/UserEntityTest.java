package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserEntity equals/hashCode")
class UserEntityTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-01-16T10:00:00Z");

    private UserEntity buildDefault() {
        return UserEntity.builder().id(1L).name("John").lastName("Doe").nickName("johndoe").email("john@example.com")
                .password("hashed-password").enabled(true).accountNonExpired(true).accountNonLocked(true)
                .credentialsNonExpired(true).activationToken("act-token-123").activationTokenExpiry(NOW)
                .passwordResetToken("reset-token-456").passwordResetTokenExpiry(NOW).passwordChangeCode("123456")
                .passwordChangeCodeExpiry(NOW).sessionRevokeCode("654321").sessionRevokeCodeExpiry(NOW)
                .sessionToRevoke("session-abc").build();
    }

    @Test
    @DisplayName("builder produces non-null entity")
    void builderProducesNonNull() {
        assertThat(buildDefault()).isNotNull();
    }

    @Nested
    @DisplayName("equals")
    class Equals {

        @Test
        @DisplayName("reflexive: a.equals(a) is true")
        @SuppressWarnings("java:S5838") // equals contract: reflexive call must use .equals() directly
        void reflexive() {
            UserEntity a = buildDefault();
            assertThat(a.equals(a)).as("equals must be reflexive").isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("not equal when id differs")
        void differentId() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setId(99L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when email differs")
        void differentEmail() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setEmail("other@example.com");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when name differs")
        void differentName() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setName("Jane");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when enabled differs")
        void differentEnabled() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setEnabled(false);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when lastName differs")
        void differentLastName() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setLastName("Smith");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when password differs")
        void differentPassword() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setPassword("different-hash");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when activationTokenExpiry differs")
        void differentActivationTokenExpiry() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setActivationTokenExpiry(LATER);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when sessionToRevoke differs")
        void differentSessionToRevoke() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setSessionToRevoke("other-session");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal to null")
        void notEqualToNull() {
            UserEntity a = buildDefault();
            assertThat(a.equals(null)).isFalse();
        }

        @Test
        @DisplayName("not equal to different class")
        void notEqualToDifferentClass() {
            UserEntity a = buildDefault();
            assertThat(a.equals("string")).isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            UserEntity a = buildDefault();
            UserEntity b = buildDefault();
            b.setId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
