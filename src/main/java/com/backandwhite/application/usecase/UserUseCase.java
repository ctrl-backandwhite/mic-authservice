package com.backandwhite.application.usecase;

import com.backandwhite.common.application.BaseUseCase;
import com.backandwhite.domain.model.User;

public interface UserUseCase extends BaseUseCase<User, User, Long> {

    default User findUserByEmail(String email) {
        return null;
    }

    User toggleEnabled(Long id);

    void activateUser(String token);

    default void activateUser(String token, String lang) {
        activateUser(token);
    }

    void requestPasswordReset(String email);

    default void requestPasswordReset(String email, String lang) {
        requestPasswordReset(email);
    }

    void resetPassword(String token, String newPassword);

    void requestPasswordChange(String email, String currentPassword, String newPassword, String confirmPassword);

    void confirmPasswordChange(String email, String code);

    default User save(User model, String lang) {
        return save(model);
    }
}
