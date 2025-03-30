package com.leaderboard;

import com.leaderboard.model.Leaderboard;
import com.leaderboard.repository.LeaderboardRepository;
import com.leaderboard.service.LeaderboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceUTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private Leaderboard existingPlayer;
    private Leaderboard newPlayer;

    @BeforeEach
    void setUp() {
        existingPlayer = new Leaderboard("existingUser", 5, 3);
        newPlayer = new Leaderboard("newUser", 0, 0);
    }

    @Test
    public void givenExistingUserWithVictory_whenUpdateLeaderboard_thenIncreaseGamesAndWinsByOne() {

        when(leaderboardRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingPlayer));
        when(leaderboardRepository.save(any(Leaderboard.class))).thenReturn(existingPlayer);

        leaderboardService.updateLeaderboard("existingUser", true);

        assertEquals(6, existingPlayer.getTotalGames());
        assertEquals(4, existingPlayer.getTotalWins());
        verify(leaderboardRepository, times(1)).save(existingPlayer);
    }

    @Test
    public void givenNewUserWithDefeat_whenUpdateLeaderboard_thenIncreaseOnlyGamesByOne() {

        when(leaderboardRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(leaderboardRepository.save(any(Leaderboard.class))).thenReturn(newPlayer);

        leaderboardService.updateLeaderboard("newUser", false);

        assertEquals(1, newPlayer.getTotalGames());
        assertEquals(0, newPlayer.getTotalWins());
        verify(leaderboardRepository, times(1)).save(newPlayer);
    }

    @Test
    public void givenRefreshLeaderboard_whenScheduledEventTime_thenUpdateLeaderboard() {

        List<Leaderboard> topPlayers = new ArrayList<>();
        topPlayers.add(existingPlayer);
        when(leaderboardRepository.findTop10ByOrderByTotalWinsDesc()).thenReturn(topPlayers);

        leaderboardService.refreshLeaderboardDaily();

        assertEquals(1, leaderboardService.getCachedTop10Players().size());
        assertEquals("existingUser", leaderboardService.getCachedTop10Players().get(0).getUsername());
    }

    @Test
    public void givenRefreshLeaderboard_whenScheduledEventTimeWithNoPlayers_thenUpdateLeaderboard() {
        when(leaderboardRepository.findTop10ByOrderByTotalWinsDesc()).thenReturn(new ArrayList<>());

        leaderboardService.refreshLeaderboardDaily();

        assertEquals(0, leaderboardService.getCachedTop10Players().size());
    }
}
