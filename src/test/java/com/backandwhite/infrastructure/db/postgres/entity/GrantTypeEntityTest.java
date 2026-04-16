package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GrantTypeEntity equals/hashCode contract")
class GrantTypeEntityTest {

    private GrantTypeEntity buildDefault() {
        return GrantTypeEntity.builder().id(1L).value("authorization_code").enabled(true).build();
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
        void reflexive() {
            GrantTypeEntity entity = buildDefault();
            assertThat(entity.equals(entity)).isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            GrantTypeEntity a = buildDefault();
            GrantTypeEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("different id yields not equal")
        void differentId() {
            GrantTypeEntity a = buildDefault();
            GrantTypeEntity b = buildDefault().withId(2L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different value yields not equal")
        void differentValue() {
            GrantTypeEntity a = buildDefault();
            GrantTypeEntity b = buildDefault().withValue("client_credentials");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different enabled yields not equal")
        void differentEnabled() {
            GrantTypeEntity a = buildDefault();
            GrantTypeEntity b = buildDefault().withEnabled(false);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("null returns false")
        void nullComparison() {
            GrantTypeEntity entity = buildDefault();
            assertThat(entity.equals(null)).isFalse();
        }

        @Test
        @DisplayName("different class returns false")
        void differentClass() {
            GrantTypeEntity entity = buildDefault();
            assertThat(entity.equals("string")).isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            GrantTypeEntity a = buildDefault();
            GrantTypeEntity b = buildDefault();
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            GrantTypeEntity a = buildDefault();
            GrantTypeEntity b = buildDefault().withId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
