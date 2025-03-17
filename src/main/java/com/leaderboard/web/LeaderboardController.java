package com.leaderboard.web;

import com.leaderboard.model.Leaderboard;
import com.leaderboard.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Autowired
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/top")
    public ResponseEntity<List<Leaderboard>> getCachedTopPlayers() {

        List<Leaderboard> topPlayers = leaderboardService.getCachedTop10Players();

        return ResponseEntity.ok(topPlayers);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateLeaderboard(@RequestParam String username, @RequestParam boolean isWinner) {

        leaderboardService.updateLeaderboard(username, isWinner);

        return ResponseEntity.ok("Leaderboard successfully updated");
    }
}
