package vn.duy.jobIT.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duy.jobIT.domain.Company;
import vn.duy.jobIT.domain.Job;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.service.CompanyService;
import vn.duy.jobIT.util.annotation.ApiMessage;


@RequestMapping(path = "${apiPrefix}/companies")
@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("")
    @ApiMessage("Create a company")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.companyService.createCompany(company));
    }

    @GetMapping("")
    @ApiMessage("Fetch all company data")
    public ResponseEntity<ResultPaginationResponse> getAllCompany(
            @Filter Specification<Company> spec,
            Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.companyService.getAllCompany(pageable, spec));
    }

    @PutMapping("")
    @ApiMessage("Update a company")
    public ResponseEntity<Company> updateCompany(
            @Valid @RequestBody Company company
    ){
        return ResponseEntity.ok(this.companyService.updateCompany(company));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a company")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable("id") Long id
    ){
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    @ApiMessage("Get company by id")
    public ResponseEntity<Company> getCompany(@PathVariable("id") Long id) throws Exception{
        return ResponseEntity.ok().body(this.companyService.findCompanyById(id));
    }
}
