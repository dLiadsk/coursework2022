package com.example.auction.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Lots")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lot {
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn
    private User user;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfLot")
    private Long idOfLot;
    @Column(name = "titleOfLot", nullable = false)
    private String titleOfLot;
    @Column(name = "idOfWinner")
    private long idOfWinner;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "firstRate")
    private double firstRate;
    @Column(name = "rate")
    private double rate;

    @Column(name = "active")
    private boolean active;

    private LocalDateTime dateAndTimeOfCreation;

    @PrePersist
    public void init() {
        dateAndTimeOfCreation = LocalDateTime.now();
    }

}
