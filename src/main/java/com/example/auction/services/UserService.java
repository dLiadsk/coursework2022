package com.example.auction.services;

import com.example.auction.models.Image;
import com.example.auction.models.User;
import com.example.auction.models.enums.Role;
import com.example.auction.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final LotService lotService;

    private final PasswordEncoder passwordEncoder;

    public User findUserById(long id) {
        return userRepository.findUserByUserId(id);
    }

    public boolean crateUser(User user, MultipartFile avatar) throws IOException {
        if (userRepository.findByEmail(user.getEmail()) != null) return false;
        log.info("{}", avatar.getSize());
        if (avatar.getSize() != 0) {
            Image image = lotService.toImageEntity(avatar);
            user.setAvatar(image);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        userRepository.save(user);
        return true;
    }
}
