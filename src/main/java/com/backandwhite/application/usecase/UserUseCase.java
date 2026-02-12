package com.backandwhite.application.usecase;

import com.backandwhite.application.BaseUseCase;
import com.backandwhite.domain.model.User;

public interface UserUseCase extends BaseUseCase<User, User, Long> {

    default User findUserByEmail(String email) {return  null; }
}
