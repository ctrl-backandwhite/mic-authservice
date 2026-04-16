package com.backandwhite.application.usecase;

import com.backandwhite.common.application.BaseUseCase;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.UserSession;
import java.util.List;

public interface UserUseCase extends BaseUseCase<User, User, Long> {

    default User findUserByEmail(String email) {
        return null;
    }

    User toggleEnabled(Long id);

    void activateUser(String token, String lang);

    void requestPasswordReset(String email, String lang);

    void resetPassword(String token, String newPassword);

    void requestPasswordChange(String email, String currentPassword, String newPassword, String confirmPassword,
            String lang);

    void confirmPasswordChange(String email, String code);

    User save(User model, String lang);

    User update(User model, Long id, String lang);

    List<UserSession> getActiveSessions(String email);

    void requestSessionRevoke(String email, String sessionId, String lang);

    void confirmSessionRevoke(String email, String code);
}
