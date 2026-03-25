package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.in.RedirectUriDtoIn;
import com.backandwhite.api.dto.out.RedirectUriDtoOut;
import com.backandwhite.api.mapper.RedirectUriDtoMapper;
import com.backandwhite.application.usecase.RedirectUriUseCase;
import com.backandwhite.domain.model.RedirectUri;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/redirecturis")
@Tag(name = "RedirectUri Operations.", description = "Operations related to redirecturis.")
public class RedirectUriController implements BaseApi<RedirectUriDtoIn, RedirectUriDtoOut, Long> {

    private final RedirectUriDtoMapper mapper;
    private final RedirectUriUseCase useCase;

    @Override
    @PostMapping
    public ResponseEntity<RedirectUriDtoOut> create(@RequestBody RedirectUriDtoIn dto) {
        RedirectUri entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<RedirectUriDtoOut> update(@RequestBody RedirectUriDtoIn dto, @PathVariable Long id) {
        RedirectUri entity = useCase.update(mapper.toDomain(dto), id);
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
    public ResponseEntity<RedirectUriDtoOut> getById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.toDtoOut(useCase.getById(id)), HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<RedirectUriDtoOut>> findAll() {
        return new ResponseEntity<>(mapper.toDtoOutList(useCase.findAll()), HttpStatus.OK);
    }
}
