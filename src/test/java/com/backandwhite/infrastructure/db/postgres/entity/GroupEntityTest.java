package com.backandwhite.infrastructure.db.postgres.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GroupEntity equals/hashCode")
class GroupEntityTest {

    private GroupEntity buildDefault() {
        return GroupEntity.builder().id(1L).name("Admins").uniqueName("ADMINS").description("Administrator group")
                .enabled(true).build();
    }

    @Nested
    @DisplayName("equals")
    class Equals {

        @Test
        @DisplayName("reflexive: a.equals(a) is true")
        void reflexive() {
            GroupEntity a = buildDefault();
            assertThat(a.equals(a)).isTrue();
        }

        @Test
        @DisplayName("symmetric: two objects with identical fields are equal")
        void sameValues() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            assertThat(a).isEqualTo(b);
            assertThat(b).isEqualTo(a);
        }

        @Test
        @DisplayName("not equal when id differs")
        void differentId() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            b.setId(99L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when name differs")
        void differentName() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            b.setName("Users");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when uniqueName differs")
        void differentUniqueName() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            b.setUniqueName("USERS");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when description differs")
        void differentDescription() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            b.setDescription("Other description");
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal when enabled differs")
        void differentEnabled() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            b.setEnabled(false);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("not equal to null")
        void notEqualToNull() {
            GroupEntity a = buildDefault();
            assertThat(a.equals(null)).isFalse();
        }

        @Test
        @DisplayName("not equal to different class")
        void notEqualToDifferentClass() {
            GroupEntity a = buildDefault();
            assertThat(a.equals("string")).isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("consistent: same values produce same hashCode")
        void consistent() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("different values may produce different hashCode")
        void differentValues() {
            GroupEntity a = buildDefault();
            GroupEntity b = buildDefault();
            b.setId(99L);
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}
