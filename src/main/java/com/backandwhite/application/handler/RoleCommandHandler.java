package com.backandwhite.application.handler;

import com.backandwhite.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class RoleCommandHandler {

    public void validate(Role role) {
        // No custom validation required for Role entity
    }

}
