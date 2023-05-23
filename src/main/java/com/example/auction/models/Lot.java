package com.example.auction.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
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


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lot")
    private List<Image> imageList = new ArrayList<>();
    private Long previewImageId;

    private LocalDateTime dateAndTimeOfCreation;

    @PrePersist
    public void init() {
        dateAndTimeOfCreation = LocalDateTime.now();
    }

    public void addImageToLot(Image image) {
        image.setLot(this);
        imageList.add(image);
    }
}
