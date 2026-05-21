package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RedirectUriEntity equals/hashCode contract")
class RedirectUriEntityTest {

    private RedirectUriEntity buildDefault() {
        return RedirectUriEntity.builder().id(1L).name("main-redirect").value("https://example.com/callback")
                .enabled(true).build();
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
            RedirectUriEntity entity = buildDefault();
            assertThat(entity.equals(entity)).as("equals must be reflexive").isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            RedirectUriEntity a = buildDefault();
            RedirectUriEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("different id yields not equal")
        void differentId() {
            RedirectUriEntity a = buildDefault();
            RedirectUriEntity b = buildDefault().withId(2L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different name yields not equal")
        void differentName() {
            RedirectUriEntity a = buildDefault();
            RedirectUriEntity b = buildDefault().withName("alt-redirect");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different value yields not equal")
        void differentValue() {
            RedirectUriEntity a = buildDefault();
            RedirectUriEntity b = buildDefault().withValue("https://other.com/callback");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different enabled yields not equal")
        void differentEnabled() {
            RedirectUriEntity a = buildDefault();
            RedirectUriEntity b = buildDefault().withEnabled(false);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("null returns false")
        @SuppressWarnings("java:S5838") // equals contract: null check must call .equals(null) directly
        void nullComparison() {
            RedirectUriEntity entity = buildDefault();
            assertThat(entity.equals(null)).as("equals(null) must be false").isFalse();
        }

        @Test
        @DisplayName("different class returns false")
        @SuppressWarnings("java:S5838") // equals contract: cross-type check must call .equals(other) directly
        void differentClass() {
            RedirectUriEntity entity = buildDefault();
            assertThat(entity.equals("string")).as("equals across types must be false").isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            RedirectUriEntity a = buildDefault();
            RedirectUriEntity b = buildDefault();
            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            RedirectUriEntity a = buildDefault();
            RedirectUriEntity b = buildDefault().withId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
