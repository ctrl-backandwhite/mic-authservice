package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.in.GroupDtoIn;
import com.backandwhite.api.dto.out.GroupDtoOut;
import com.backandwhite.api.mapper.GroupDtoMapper;
import com.backandwhite.application.usecase.GroupUseCase;
import com.backandwhite.common.security.annotation.NxAdmin;
import com.backandwhite.domain.model.Group;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@NxAdmin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
@Tag(name = "Group Operations.", description = "Operations related to groups.")
public class GroupController implements BaseApi<GroupDtoIn, GroupDtoOut, Long> {

    private final GroupDtoMapper mapper;
    private final GroupUseCase useCase;

    @Override
    @PostMapping
    public ResponseEntity<GroupDtoOut> create(@RequestBody GroupDtoIn dto) {
        Group entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<GroupDtoOut> update(@RequestBody GroupDtoIn dto, @PathVariable Long id) {
        Group entity = useCase.update(mapper.toDomain(dto), id);
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
    public ResponseEntity<GroupDtoOut> getById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.toDtoOut(useCase.getById(id)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GroupDtoOut>> findAll(@RequestParam(required = false) Boolean enabled) {
        List<Group> groups = useCase.findAll();
        if (enabled != null) {
            groups = groups.stream().filter(g -> enabled.equals(g.getEnabled())).toList();
        }
        return new ResponseEntity<>(mapper.toDtoOutList(groups), HttpStatus.OK);
    }
}
