package com.backandwhite.application.handler;

import com.backandwhite.domain.model.Scope;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ScopeCommandHandler {

    public void validate(Scope scope) {
        // No custom validation required for Scope entity
    }

}
