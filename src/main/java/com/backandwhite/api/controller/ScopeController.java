package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.in.ScopeDtoIn;
import com.backandwhite.api.dto.out.ScopeDtoOut;
import com.backandwhite.api.mapper.ScopeDtoMapper;
import com.backandwhite.application.usecase.ScopeUseCase;
import com.backandwhite.domain.model.Scope;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scopes")
@Tag(name = "Scope Operations.", description = "Operations related to scopes.")
public class ScopeController implements BaseApi<ScopeDtoIn, ScopeDtoOut, Long> {

    private final ScopeDtoMapper mapper;
    private final ScopeUseCase useCase;

    @Override
    @PostMapping
    public ResponseEntity<ScopeDtoOut> create(@RequestBody ScopeDtoIn dto) {
        Scope entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ScopeDtoOut> update(@RequestBody ScopeDtoIn dto, @PathVariable Long id) {
        Scope entity = useCase.update(mapper.toDomain(dto), id);
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
    public ResponseEntity<ScopeDtoOut> getById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.toDtoOut(useCase.getById(id)), HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ScopeDtoOut>> findAll() {
        return new ResponseEntity<>(mapper.toDtoOutList(useCase.findAll()), HttpStatus.OK);
    }
}
