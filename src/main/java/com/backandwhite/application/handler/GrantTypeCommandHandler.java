package com.backandwhite.application.handler;

import com.backandwhite.domain.model.GrantType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class GrantTypeCommandHandler {

    public void validate(GrantType grantType) {
    }

}
