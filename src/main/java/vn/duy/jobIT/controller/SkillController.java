package vn.duy.jobIT.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duy.jobIT.domain.Skill;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.service.SkillService;
import vn.duy.jobIT.util.annotation.ApiMessage;
import vn.duy.jobIT.util.error.IdInvalidException;

@RequestMapping(path = "${apiPrefix}/skills")
@RequiredArgsConstructor
@RestController
public class SkillController {
    private final SkillService skillService;

    @PostMapping("")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill skill) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.create(skill));
    }

    @PutMapping("")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill skill) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.update(skill));
    }

    @GetMapping("")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationResponse> getAll(
            @Filter Specification<Skill> spec,
            Pageable pageable
            ){
        return ResponseEntity.status(HttpStatus.OK).body(
                this.skillService.fetchAllSkill(spec, pageable)
        );
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}
