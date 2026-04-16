package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.in.RoleDtoIn;
import com.backandwhite.api.dto.out.RoleDtoOut;
import com.backandwhite.api.mapper.RoleDtoMapper;
import com.backandwhite.application.usecase.RoleUseCase;
import com.backandwhite.common.security.annotation.NxAdmin;
import com.backandwhite.domain.model.Role;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@NxAdmin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
@Tag(name = "Role Operations.", description = "Operations related to roles.")
public class RoleController implements BaseApi<RoleDtoIn, RoleDtoOut, Long> {

    private final RoleDtoMapper mapper;
    private final RoleUseCase useCase;

    @Override
    @PostMapping
    public ResponseEntity<RoleDtoOut> create(@RequestBody RoleDtoIn dto) {
        Role entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<RoleDtoOut> update(@RequestBody RoleDtoIn dto, @PathVariable Long id) {
        Role entity = useCase.update(mapper.toDomain(dto), id);
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<RoleDtoOut> getById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.toDtoOut(useCase.getById(id)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RoleDtoOut>> findAll(@RequestParam(required = false) Boolean enabled) {
        List<Role> roles = useCase.findAll();
        if (enabled != null) {
            roles = roles.stream().filter(r -> enabled.equals(r.getEnabled())).toList();
        }
        return new ResponseEntity<>(mapper.toDtoOutList(roles), HttpStatus.OK);
    }
}
