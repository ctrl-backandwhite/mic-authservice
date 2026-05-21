package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RoleEntity equals/hashCode")
class RoleEntityTest {

    private RoleEntity buildDefault() {
        return RoleEntity.builder().id(1L).name("Admin").uniqueName("ROLE_ADMIN").description("Administrator role")
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
        @DisplayName("reflexive: a.equals(a) is true")
        @SuppressWarnings("java:S5838") // equals contract: reflexive call must use .equals() directly
        void reflexive() {
            RoleEntity a = buildDefault();
            assertThat(a.equals(a)).as("equals must be reflexive").isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("not equal when id differs")
        void differentId() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            b.setId(99L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when name differs")
        void differentName() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            b.setName("User");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when uniqueName differs")
        void differentUniqueName() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            b.setUniqueName("ROLE_USER");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when description differs")
        void differentDescription() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            b.setDescription("Other description");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when enabled differs")
        void differentEnabled() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            b.setEnabled(false);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal to null")
        void notEqualToNull() {
            RoleEntity a = buildDefault();
            assertThat(a.equals(null)).isFalse();
        }

        @Test
        @DisplayName("not equal to different class")
        void notEqualToDifferentClass() {
            RoleEntity a = buildDefault();
            assertThat(a.equals("string")).isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            RoleEntity a = buildDefault();
            RoleEntity b = buildDefault();
            b.setId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
