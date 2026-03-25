package com.backandwhite.application.usecase;

import com.backandwhite.common.application.BaseUseCase;
import com.backandwhite.domain.model.User;

public interface UserUseCase extends BaseUseCase<User, User, Long> {

    default User findUserByEmail(String email) {
        return null;
    }

    User toggleEnabled(Long id);

    void activateUser(String token);

    void requestPasswordReset(String email);

    default void requestPasswordReset(String email, String lang) {
        requestPasswordReset(email);
    }

    void resetPassword(String token, String newPassword);

    default User save(User model, String lang) {
        return save(model);
    }
}
