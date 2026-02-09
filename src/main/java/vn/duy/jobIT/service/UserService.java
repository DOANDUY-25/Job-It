package vn.duy.jobIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duy.jobIT.domain.Company;
import vn.duy.jobIT.domain.Role;
import vn.duy.jobIT.domain.User;
import vn.duy.jobIT.domain.dto.UpdateUserDTO;
import vn.duy.jobIT.domain.res.user.CreatedUserResponse;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.domain.res.user.UpdatedUserResponse;
import vn.duy.jobIT.repository.UserRepository;
import vn.duy.jobIT.util.convert.UserConvert;
import vn.duy.jobIT.util.error.DuplicateResourceException;
import vn.duy.jobIT.util.error.ResourceNotFoundException;
import vn.duy.jobIT.util.response.FormatResultPagaination;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public CreatedUserResponse createUser(User user) {
        String email = user.getEmail();

        // Check if email already exists
        if(userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }

        // Set company if provided
        if(user.getCompany() != null){
            Company company = this.companyService.findCompanyById(user.getCompany().getId());
            user.setCompany(company);
        }

        // Set role if provided
        if(user.getRole() != null){
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role);
        }

        // Hash password
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        return UserConvert.convertToResCreatedUserRes(this.userRepository.save(user));
    }

    public CreatedUserResponse fetchUserById(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserConvert.convertToResCreatedUserRes(user);
    }

    public void deleteUser(Long id) {
        if(!this.userRepository.existsById(id)){
            throw new ResourceNotFoundException("User", "id", id);
        }
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username) {
        User user = this.userRepository.findByEmail(username);
        if(user == null) {
            throw new ResourceNotFoundException("User", "email", username);
        }
        return user;
    }

    public ResultPaginationResponse getAllUser(Pageable pageable, Specification<User> spec){
        Page<User> userPage = this.userRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginateUserRes(userPage);
    }

    public UpdatedUserResponse updateUser(Long id, UpdateUserDTO user) {
        User currentUser = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        currentUser.setName(user.getName());
        currentUser.setGender(user.getGender());
        currentUser.setAge(user.getAge());
        currentUser.setAddress(user.getAddress());

        // Update company if provided
        if(user.getCompany() != null){
            Company company = this.companyService.findCompanyById(user.getCompany().getId());
            currentUser.setCompany(company);
        }

        // Update role if provided
        if(user.getRole() != null){
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            currentUser.setRole(role);
        }

        return UserConvert.convertToResUpdatedUserRes(this.userRepository.save(currentUser));
    }

    public void updateUserToken(String token, String email){
        User user = this.userRepository.findByEmail(email);
        if(user != null){
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email){
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
