package com.leaderboard.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private int totalGames;

    @Column(nullable = false)
    private int totalWins;

    public Leaderboard(String username, int totalGames, int totalWins) {
        this.username = username;
        this.totalGames = totalGames;
        this.totalWins = totalWins;
    }
}
