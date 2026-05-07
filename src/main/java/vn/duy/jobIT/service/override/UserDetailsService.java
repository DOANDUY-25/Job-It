package vn.duy.jobIT.service.override;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.duy.jobIT.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        vn.duy.jobIT.domain.User user = this.userService.handleGetUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("username / password not found");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
