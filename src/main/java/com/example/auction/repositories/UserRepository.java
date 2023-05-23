package com.example.auction.repositories;

import com.example.auction.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findUserByUserId(long id);
}
