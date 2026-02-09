package vn.duy.jobIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duy.jobIT.domain.Permission;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.repository.PermissionRepository;
import vn.duy.jobIT.util.error.DuplicateResourceException;
import vn.duy.jobIT.util.error.ResourceNotFoundException;
import vn.duy.jobIT.util.response.FormatResultPagaination;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission create(Permission permission){
        if(this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        )){
            throw new DuplicateResourceException(
                String.format("Permission already exists with module: %s, apiPath: %s, method: %s", 
                    permission.getModule(), permission.getApiPath(), permission.getMethod())
            );
        }
        return this.permissionRepository.save(permission);
    }

    public Permission fetchPermissionById(Long id) {
        return this.permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
    }

    public Permission update(Permission permission){
        Permission currentPermission = this.fetchPermissionById(permission.getId());

        // Check if the new combination already exists (excluding current permission)
        boolean isDuplicate = this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );
        
        boolean isSameCombination = currentPermission.getModule().equals(permission.getModule()) &&
                                   currentPermission.getApiPath().equals(permission.getApiPath()) &&
                                   currentPermission.getMethod().equals(permission.getMethod());
        
        if(isDuplicate && !isSameCombination){
            throw new DuplicateResourceException(
                String.format("Permission already exists with module: %s, apiPath: %s, method: %s", 
                    permission.getModule(), permission.getApiPath(), permission.getMethod())
            );
        }

        // Check if name already exists (excluding current permission)
        if(!currentPermission.getName().equals(permission.getName()) && 
           this.permissionRepository.existsByName(permission.getName())){
            throw new DuplicateResourceException("Permission", "name", permission.getName());
        }

        currentPermission.setApiPath(permission.getApiPath());
        currentPermission.setName(permission.getName());
        currentPermission.setModule(permission.getModule());
        currentPermission.setMethod(permission.getMethod());

        return this.permissionRepository.save(currentPermission);
    }

    public void delete(Long id) {
        Permission permission = this.fetchPermissionById(id);

        // Remove permission from all roles
        permission.getRoles().forEach(
                role -> role.getPermissions().remove(permission)
        );
        
        this.permissionRepository.delete(permission);
    }

    public ResultPaginationResponse fetchAll(Specification<Permission> spec, Pageable pageable){
        Page<Permission> permissionPage = this.permissionRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginationResponse(permissionPage);
    }
}
