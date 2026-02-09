package vn.duy.jobIT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.duy.jobIT.domain.Company;
import vn.duy.jobIT.domain.User;

import java.util.List;

@Repository
public interface CompanyRepository
        extends
        JpaRepository<Company, Long>,
        JpaSpecificationExecutor<Company>
{
}
