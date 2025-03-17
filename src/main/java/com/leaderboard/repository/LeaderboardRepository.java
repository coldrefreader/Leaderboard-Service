package com.leaderboard.repository;

import com.leaderboard.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {

    @Query("SELECT l FROM Leaderboard l ORDER BY l.totalWins DESC")
    List<Leaderboard> findTop10ByOrderByTotalWinsDesc();

    Optional<Leaderboard> findByUsername(String username);
}
