package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.in.PermissionDtoIn;
import com.backandwhite.api.dto.out.PermissionDtoOut;
import com.backandwhite.api.mapper.PermissionDtoMapper;
import com.backandwhite.application.usecase.PermissionUseCase;
import com.backandwhite.domain.model.Permission;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permission Operations.", description = "Operations related to permissions.")
public class PermissionController implements BaseApi<PermissionDtoIn, PermissionDtoOut, Long> {

    private final PermissionDtoMapper mapper;
    private final PermissionUseCase useCase;

    @Override
    @PostMapping
    public ResponseEntity<PermissionDtoOut> create(@RequestBody PermissionDtoIn dto) {
        Permission entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<PermissionDtoOut> update(@RequestBody PermissionDtoIn dto, @PathVariable Long id) {
        Permission entity = useCase.update(mapper.toDomain(dto), id);
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
    public ResponseEntity<PermissionDtoOut> getById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.toDtoOut(useCase.getById(id)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PermissionDtoOut>> findAll(
            @RequestParam(required = false) Boolean enabled) {
        List<Permission> permissions = useCase.findAll();
        if (enabled != null) {
            permissions = permissions.stream().filter(p -> enabled.equals(p.getEnabled())).toList();
        }
        return new ResponseEntity<>(mapper.toDtoOutList(permissions), HttpStatus.OK);
    }
}
