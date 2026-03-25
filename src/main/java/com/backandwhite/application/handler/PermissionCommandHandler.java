package com.backandwhite.application.handler;

import com.backandwhite.domain.model.Permission;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class PermissionCommandHandler {

    public void validate(Permission permission) {
    }
}
