package vn.duy.jobIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duy.jobIT.domain.Permission;
import vn.duy.jobIT.domain.Role;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.repository.PermissionRepository;
import vn.duy.jobIT.repository.RoleRepository;
import vn.duy.jobIT.util.error.DuplicateResourceException;
import vn.duy.jobIT.util.error.ResourceNotFoundException;
import vn.duy.jobIT.util.response.FormatResultPagaination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role create(Role role){
        if(this.roleRepository.existsByName(role.getName())){
            throw new DuplicateResourceException("Role", "name", role.getName());
        }
        
        if(role.getPermissions() != null){
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        
        return this.roleRepository.save(role);
    }

    public Role fetchRoleById(Long id) {
        return this.roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    public Role update(Role role){
        Role currentRole = this.fetchRoleById(role.getId());

        if(role.getPermissions() != null){
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            currentRole.setPermissions(dbPermissions);
        }

        currentRole.setName(role.getName());
        currentRole.setActive(role.isActive());
        currentRole.setDescription(role.getDescription());

        return this.roleRepository.save(currentRole);
    }

    public void delete(Long id) {
        Role role = this.fetchRoleById(id);
        this.roleRepository.delete(role);
    }

    public ResultPaginationResponse fetchAll(Specification<Role> spec, Pageable pageable){
        Page<Role> rolePage = this.roleRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginationResponse(rolePage);
    }
}
