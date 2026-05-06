package com.helpdesk.repository;

import com.helpdesk.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    List<AppUser> findByUsernameContainingIgnoreCase(String username);
    List<AppUser> findByRole(String role);
    List<AppUser> findByUsernameContainingIgnoreCaseAndRole(String username, String role);
}
