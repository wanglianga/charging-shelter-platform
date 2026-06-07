package com.charging.shelter.service;

import com.charging.shelter.entity.User;
import com.charging.shelter.enums.UserRole;
import com.charging.shelter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> findBlacklisted() {
        return userRepository.findByBlacklistedTrue();
    }

    public User save(User user) {
        if (user.getBlacklisted() == null) {
            user.setBlacklisted(false);
        }
        return userRepository.save(user);
    }

    public User setBlacklist(Long userId, boolean blacklisted) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setBlacklisted(blacklisted);
            return userRepository.save(user);
        }
        throw new RuntimeException("用户不存在");
    }
}
