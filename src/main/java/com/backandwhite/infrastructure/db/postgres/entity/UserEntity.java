package com.backandwhite.infrastructure.db.postgres.entity;

import com.backandwhite.common.infrastructure.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@With
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "last_name", length = 60)
    private String lastName;

    @Column(name = "nick_name", length = 60, unique = true)
    private String nickName;

    @Column(name = "email", unique = true, length = 120)
    private String email;

    @Column(name = "password", length = 250)
    private String password;

    @Column(name = "enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled;

    @Column(name = "account_non_expired", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean accountNonExpired;

    @Column(name = "account_non_locked", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean accountNonLocked;

    @Column(name = "credentials_non_expired", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean credentialsNonExpired;

    @Column(name = "activation_token", length = 64, unique = true)
    private String activationToken;

    @Column(name = "activation_token_expiry")
    private Instant activationTokenExpiry;

    @Column(name = "password_reset_token", length = 64, unique = true)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private Instant passwordResetTokenExpiry;

    @Column(name = "password_change_code", length = 6)
    private String passwordChangeCode;

    @Column(name = "password_change_code_expiry")
    private Instant passwordChangeCodeExpiry;

    @Column(name = "session_revoke_code", length = 6)
    private String sessionRevokeCode;

    @Column(name = "session_revoke_code_expiry")
    private Instant sessionRevokeCodeExpiry;

    @Column(name = "session_to_revoke", length = 64)
    private String sessionToRevoke;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<RoleEntity> roles = new ArrayList<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<GroupEntity> groups = new ArrayList<>();

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass())
            return false;
        UserEntity that = (UserEntity) object;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(nickName, that.nickName)
                && Objects.equals(email, that.email)
                && Objects.equals(password, that.password)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(accountNonExpired, that.accountNonExpired)
                && Objects.equals(accountNonLocked, that.accountNonLocked)
                && Objects.equals(credentialsNonExpired, that.credentialsNonExpired)
                && Objects.equals(activationToken, that.activationToken)
                && Objects.equals(activationTokenExpiry, that.activationTokenExpiry)
                && Objects.equals(passwordResetToken, that.passwordResetToken)
                && Objects.equals(passwordResetTokenExpiry, that.passwordResetTokenExpiry)
                && Objects.equals(passwordChangeCode, that.passwordChangeCode)
                && Objects.equals(passwordChangeCodeExpiry, that.passwordChangeCodeExpiry)
                && Objects.equals(sessionRevokeCode, that.sessionRevokeCode)
                && Objects.equals(sessionRevokeCodeExpiry, that.sessionRevokeCodeExpiry)
                && Objects.equals(sessionToRevoke, that.sessionToRevoke);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, nickName, email, password, enabled, accountNonExpired, accountNonLocked,
                credentialsNonExpired, activationToken, activationTokenExpiry,
                passwordResetToken, passwordResetTokenExpiry,
                passwordChangeCode, passwordChangeCodeExpiry,
                sessionRevokeCode, sessionRevokeCodeExpiry, sessionToRevoke);
    }
}
