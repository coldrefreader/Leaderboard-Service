package com.leaderboard.service;

import com.leaderboard.model.Leaderboard;
import com.leaderboard.repository.LeaderboardRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private List<Leaderboard> cachedTop10Players = new ArrayList<>();

    @Autowired
    public LeaderboardService(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @PostConstruct
    public void init() {
        refreshLeaderboardDaily();
    }


    @Transactional
    public void updateLeaderboard(String username, boolean isWinner) {

        log.info("Received request to update leaderboard for username {}", username);

        Leaderboard player = leaderboardRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.info("Creating new leaderboard for username {}", username);
                    Leaderboard newPlayer = new Leaderboard(username, 0, 0);
                    return leaderboardRepository.save(newPlayer);
                });


        player.setTotalGames(player.getTotalGames() + 1);
        if (isWinner) {
            player.setTotalWins(player.getTotalWins() + 1);
        }

        leaderboardRepository.save(player);
        log.info("Successfully updated leaderboard for user {} ", username);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshLeaderboardDaily() {

        log.info("Daily request to update the leaderboard");

        List<Leaderboard> topPlayers = leaderboardRepository.findTop10ByOrderByTotalWinsDesc();

        if (topPlayers.isEmpty()) {
            log.info("No players found in the leaderboard.");
            return;
        }
        for (int i = 0; i < topPlayers.size(); i++) {
            Leaderboard player = topPlayers.get(i);
            log.info("#{} {} - Games: {} | Wins: {}", (i + 1), player.getUsername(), player.getTotalGames(), player.getTotalWins());

        }
        cachedTop10Players.clear();
        cachedTop10Players.addAll(topPlayers);
        log.info("Successfully updated cache of top 10 players");
    }

    public List<Leaderboard> getCachedTop10Players() {
        return cachedTop10Players;
    }
}