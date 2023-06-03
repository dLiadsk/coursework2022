package com.example.auction.services;

import com.example.auction.models.User;
import com.example.auction.models.enums.Role;
import com.example.auction.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    public User findUserById(long id) {
        return userRepository.findUserByUserId(id);
    }

    public boolean createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) return false;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        userRepository.save(user);
        return true;
    }
}
