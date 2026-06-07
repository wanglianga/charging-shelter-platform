package com.charging.shelter.repository;

import com.charging.shelter.entity.User;
import com.charging.shelter.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    List<User> findByRole(UserRole role);

    List<User> findByIsBlacklistedTrue();
}
