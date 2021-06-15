package com.dd.auth.api.service;

import com.dd.auth.api.model.Login;
import com.dd.auth.api.repository.LoginRepository;
import com.dd.auth.api.repository.PermissionRepository;
import com.dd.auth.api.repository.PermissionSetsRepository;
import com.dd.auth.api.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@AllArgsConstructor
@Slf4j
public class ProfileUserDetailService implements UserDetailsService {

    private final LoginRepository loginRepository;
    private final PermissionSetsRepository permissionSetsRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Login> optionalUser = loginRepository.findByUsername(username);
        Login login = optionalUser.orElseThrow(() ->
                new UsernameNotFoundException("No user found with name " + username));
        return new User(login.getUsername(), login.getPassphrase().trim(), getAuthorities(login.getUserId()));
    }

    private Set<GrantedAuthority> getAuthorities(Long loginId) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        permissionSetsRepository.findAllByLoginId(loginId).forEach(ps -> {
            String rolePerm = String.join(" ", roleRepository.findById(ps.getRoleId()).get().getName(),
                    permissionRepository.findById(ps.getPermId()).get().getName());
            authorities.add(new SimpleGrantedAuthority(rolePerm));
        });
        log.info("Authorities accessed..." + authorities);
        return authorities;
    }
}
