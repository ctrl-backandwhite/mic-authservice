package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PermissionEntity equals/hashCode")
class PermissionEntityTest {

    private PermissionEntity buildDefault() {
        return PermissionEntity.builder().id(1L).name("Read").uniqueName("PERM_READ").description("Read permission")
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
        void reflexive() {
            PermissionEntity a = buildDefault();
            assertThat(a.equals(a)).isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("not equal when id differs")
        void differentId() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            b.setId(99L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when name differs")
        void differentName() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            b.setName("Write");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when uniqueName differs")
        void differentUniqueName() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            b.setUniqueName("PERM_WRITE");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when description differs")
        void differentDescription() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            b.setDescription("Other description");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when enabled differs")
        void differentEnabled() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            b.setEnabled(false);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal to null")
        void notEqualToNull() {
            PermissionEntity a = buildDefault();
            assertThat(a.equals(null)).isFalse();
        }

        @Test
        @DisplayName("not equal to different class")
        void notEqualToDifferentClass() {
            PermissionEntity a = buildDefault();
            assertThat(a.equals("string")).isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            PermissionEntity a = buildDefault();
            PermissionEntity b = buildDefault();
            b.setId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
