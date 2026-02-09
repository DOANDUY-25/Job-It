package vn.duy.jobIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.duy.jobIT.domain.Company;
import vn.duy.jobIT.domain.Role;
import vn.duy.jobIT.domain.User;
import vn.duy.jobIT.domain.dto.UpdateUserDTO;
import vn.duy.jobIT.domain.res.user.CompanyUser;
import vn.duy.jobIT.domain.res.user.CreatedUserResponse;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.domain.res.user.UpdatedUserResponse;
import vn.duy.jobIT.repository.UserRepository;
import vn.duy.jobIT.util.convert.UserConvert;
import vn.duy.jobIT.util.error.IdInvalidException;
import vn.duy.jobIT.util.response.FormatResultPagaination;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public CreatedUserResponse createUser(User user) throws Exception {
        String email = user.getEmail();

        if(user.getCompany() != null){
            Company company = this.companyService.findCompanyById(user.getCompany().getId());
            user.setCompany(company);
        }

        if(userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        if(user.getRole() != null){
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role);
        }

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        return UserConvert.convertToResCreatedUserRes(this.userRepository.save(user));
    }

    public CreatedUserResponse fetchUserById(Long id) throws Exception {
        if(userRepository.existsById(id)){
            return UserConvert.convertToResCreatedUserRes(this.userRepository.findById(id).get());
        }else{
            throw new IdInvalidException("The specified User ID is invalid");
        }
    }

    public void deleteUser(Long id) throws Exception {
        if(this.userRepository.existsById(id)){
            this.userRepository.deleteById(id);
        }else{
         throw new IdInvalidException("The specified User ID is invalid");
        }
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResultPaginationResponse getAllUser(Pageable pageable, Specification<User> spec){
        Page<User> userPage = this.userRepository.findAll(spec, pageable);

        return FormatResultPagaination.createPaginateUserRes(userPage);
    }

    public UpdatedUserResponse updateUser(Long id, UpdateUserDTO user) throws Exception {
        Optional<User> userOptional = this.userRepository.findById(id);
        if(userOptional.isPresent()){
            User currentUser = userOptional.get();
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAge(user.getAge());
            currentUser.setAddress(user.getAddress());

            if(user.getCompany() != null){
                Company company = this.companyService.findCompanyById(user.getCompany().getId());
                if(company == null){
                    throw new IdInvalidException("Company Id is invalid");
                }
                currentUser.setCompany(company);
            }

            if(user.getRole() != null){
                Role role = this.roleService.fetchRoleById(user.getRole().getId());
                user.setRole(role);
            }

            return UserConvert.convertToResUpdatedUserRes(this.userRepository.save(currentUser));
        }
        return null;
    }

    public void updateUserToken(String token, String email){
        User user = this.handleGetUserByUsername(email);
        if(user != null){
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email){
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
