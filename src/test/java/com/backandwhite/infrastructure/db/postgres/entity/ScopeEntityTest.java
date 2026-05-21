package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ScopeEntity equals/hashCode contract")
class ScopeEntityTest {

    private ScopeEntity buildDefault() {
        return ScopeEntity.builder().id(1L).name("read").uniqueName("scope:read").description("Read-only access")
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
            ScopeEntity entity = buildDefault();
            assertThat(entity.equals(entity)).as("equals must be reflexive").isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("different id yields not equal")
        void differentId() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault().withId(2L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different name yields not equal")
        void differentName() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault().withName("write");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different uniqueName yields not equal")
        void differentUniqueName() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault().withUniqueName("scope:write");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different description yields not equal")
        void differentDescription() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault().withDescription("Full access");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("different enabled yields not equal")
        void differentEnabled() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault().withEnabled(false);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("null returns false")
        @SuppressWarnings("java:S5838") // equals contract: null check must call .equals(null) directly
        void nullComparison() {
            ScopeEntity entity = buildDefault();
            assertThat(entity.equals(null)).as("equals(null) must be false").isFalse();
        }

        @Test
        @DisplayName("different class returns false")
        @SuppressWarnings("java:S5838") // equals contract: cross-type check must call .equals(other) directly
        void differentClass() {
            ScopeEntity entity = buildDefault();
            assertThat(entity.equals("string")).as("equals across types must be false").isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault();
            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            ScopeEntity a = buildDefault();
            ScopeEntity b = buildDefault().withId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
