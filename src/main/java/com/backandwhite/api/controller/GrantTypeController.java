package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.in.GrantTypeDtoIn;
import com.backandwhite.api.dto.out.GrantTypeDtoOut;
import com.backandwhite.api.mapper.GrantTypeDtoMapper;
import com.backandwhite.application.usecase.GrantTypeUseCase;
import com.backandwhite.domain.model.GrantType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/granttypes")
@Tag(name = "GrantType Operations.", description = "Operations related to granttypes.")
public class GrantTypeController implements BaseApi<GrantTypeDtoIn, GrantTypeDtoOut, Long> {

    private final GrantTypeDtoMapper mapper;
    private final GrantTypeUseCase useCase;

    @Override
    @PostMapping
    public ResponseEntity<GrantTypeDtoOut> create(@RequestBody GrantTypeDtoIn dto) {
        GrantType entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<GrantTypeDtoOut> update(@RequestBody GrantTypeDtoIn dto, @PathVariable Long id) {
        GrantType entity = useCase.update(mapper.toDomain(dto), id);
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
    public ResponseEntity<GrantTypeDtoOut> getById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.toDtoOut(useCase.getById(id)), HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<GrantTypeDtoOut>> findAll() {
        return new ResponseEntity<>(mapper.toDtoOutList(useCase.findAll()), HttpStatus.OK);
    }
}
