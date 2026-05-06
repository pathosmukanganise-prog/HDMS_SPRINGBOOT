package com.helpdesk.service;

import com.helpdesk.model.AppUser;
import com.helpdesk.repository.AppUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountService implements UserDetailsService {
    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(AppUserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void seedDefaultUsers() {
        createUser("user", "1234", "USER");
        createUser("admin", "admin123", "ADMIN");
    }

    public List<String> getUsernames() {
        return repo.findAll().stream().map(AppUser::getUsername).toList();
    }

    public List<AppUser> getUsers() {
        return repo.findAll();
    }

    public List<AppUser> searchUsers(String query, String role) {
        String cleanQuery = query == null ? "" : query.trim();
        String cleanRole = "ADMIN".equals(role) || "USER".equals(role) ? role : "";
        if (!cleanQuery.isBlank() && !cleanRole.isBlank()) {
            return repo.findByUsernameContainingIgnoreCaseAndRole(cleanQuery, cleanRole);
        }
        if (!cleanQuery.isBlank()) {
            return repo.findByUsernameContainingIgnoreCase(cleanQuery);
        }
        if (!cleanRole.isBlank()) {
            return repo.findByRole(cleanRole);
        }
        return getUsers();
    }

    public boolean createUser(String username, String password, String role) {
        String cleanUsername = username == null ? "" : username.trim();
        String cleanRole = "ADMIN".equals(role) ? "ADMIN" : "USER";
        if (cleanUsername.isBlank() || password == null || password.isBlank() || repo.existsByUsername(cleanUsername)) {
            return false;
        }
        AppUser user = new AppUser();
        user.setUsername(cleanUsername);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(cleanRole);
        repo.save(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
