package vn.duy.jobIT.util.security;

import org.springframework.data.domain.AuditorAware;
import vn.duy.jobIT.util.security.SecurityUtils;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserLogin().orElse("system"));
    }
}
