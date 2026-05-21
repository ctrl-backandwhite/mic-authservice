package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("OauthClientEntity equals/hashCode contract")
class OauthClientEntityTest {

    private OauthClientEntity buildDefault() {
        return OauthClientEntity.builder().id(1L).clientId("web-app").clientSecret("s3cr3t").build();
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
        @DisplayName("reflexive: x.equals(x) is true")
        @SuppressWarnings("java:S5838") // equals contract: reflexive call must use .equals() directly
        void reflexive() {
            OauthClientEntity entity = buildDefault();
            assertThat(entity.equals(entity)).as("equals must be reflexive").isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            OauthClientEntity a = buildDefault();
            OauthClientEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("different id yields not equal")
        void differentId() {
            OauthClientEntity a = buildDefault();
            OauthClientEntity b = buildDefault().withId(2L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different clientId yields not equal")
        void differentClientId() {
            OauthClientEntity a = buildDefault();
            OauthClientEntity b = buildDefault().withClientId("mobile-app");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different clientSecret yields not equal")
        void differentClientSecret() {
            OauthClientEntity a = buildDefault();
            OauthClientEntity b = buildDefault().withClientSecret("oth3r");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("null returns false")
        @SuppressWarnings("java:S5838") // equals contract: null check must call .equals(null) directly
        void nullComparison() {
            OauthClientEntity entity = buildDefault();
            assertThat(entity.equals(null)).as("equals(null) must be false").isFalse();
        }

        @Test
        @DisplayName("different class returns false")
        @SuppressWarnings("java:S5838") // equals contract: cross-type check must call .equals(other) directly
        void differentClass() {
            OauthClientEntity entity = buildDefault();
            assertThat(entity.equals("string")).as("equals across types must be false").isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            OauthClientEntity a = buildDefault();
            OauthClientEntity b = buildDefault();
            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            OauthClientEntity a = buildDefault();
            OauthClientEntity b = buildDefault().withId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
