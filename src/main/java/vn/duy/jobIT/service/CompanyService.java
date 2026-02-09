package vn.duy.jobIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duy.jobIT.domain.Company;
import vn.duy.jobIT.domain.User;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.repository.CompanyRepository;
import vn.duy.jobIT.repository.UserRepository;
import vn.duy.jobIT.util.error.ResourceNotFoundException;
import vn.duy.jobIT.util.response.FormatResultPagaination;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public Company createCompany(Company company){
        return companyRepository.save(company);
    }

    public ResultPaginationResponse getAllCompany(Pageable pageable, Specification<Company> spec){
        Page<Company> companyPage = companyRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginationResponse(companyPage);
    }

    public Company findCompanyById(Long id){
        return this.companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    public Company updateCompany(Company company){
        Company currentCompany = this.companyRepository.findById(company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", company.getId()));
        
        currentCompany.setName(company.getName());
        currentCompany.setLogo(company.getLogo());
        currentCompany.setDescription(company.getDescription());
        currentCompany.setAddress(company.getAddress());
        
        return this.companyRepository.save(currentCompany);
    }

    public void deleteCompany(Long id){
        Company company = this.companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        
        // Delete all users associated with this company
        List<User> users = this.userRepository.findByCompany(company);
        this.userRepository.deleteAll(users);
        
        // Delete the company
        this.companyRepository.deleteById(id);
    }
}
