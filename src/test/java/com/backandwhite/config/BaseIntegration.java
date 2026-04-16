package com.backandwhite.config;

import com.backandwhite.core.test.JwtTestUtil;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Auth-service integration test base. Extends the shared core BaseIntegration
 * and adds: - @Import of the local TestContainersConfiguration - getToken()
 * helper delegating to the injected JwtTestUtil
 */
@Import(TestContainersConfiguration.class)
public abstract class BaseIntegration extends com.backandwhite.core.test.BaseIntegration {

    @Autowired
    private JwtTestUtil jwtTestUtil;

    public String getToken(List<String> roles) {
        return jwtTestUtil.getToken("John Doe", roles);
    }

    public String getToken(List<String> roles, String email) {
        return jwtTestUtil.getToken("John Doe", roles, Map.of("email", email));
    }
}
