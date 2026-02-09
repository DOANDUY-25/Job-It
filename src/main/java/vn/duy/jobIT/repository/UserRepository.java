package vn.duy.jobIT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.duy.jobIT.domain.Company;
import vn.duy.jobIT.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String token,String email);
    List<User> findByCompany(Company company);
}
