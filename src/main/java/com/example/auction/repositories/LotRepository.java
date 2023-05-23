package com.example.auction.repositories;

import com.example.auction.models.Lot;
import com.example.auction.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {
    List<Lot> findAllByActive(boolean active);

    List<Lot> findAllByUser(User user);

    List<Lot> findByDescriptionContainingIgnoreCaseAndActive(String keyword, boolean active);
}
