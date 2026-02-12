package com.backandwhite.domain.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolUtil {

    public static final String ADMIN= "hasAnyRole('ADMIN')";
    public static final String ADMIN_USER_GUEST = "hasAnyRole('ADMIN','USER','GUEST')";
}
