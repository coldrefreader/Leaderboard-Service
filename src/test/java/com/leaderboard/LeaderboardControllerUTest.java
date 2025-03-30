package com.leaderboard;

import com.leaderboard.model.Leaderboard;
import com.leaderboard.service.LeaderboardService;
import com.leaderboard.web.LeaderboardController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class LeaderboardControllerUTest {

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private LeaderboardController leaderboardController;

    private MockMvc mockMvc;

    private List<Leaderboard> topPlayers;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(leaderboardController).build();
        topPlayers = Arrays.asList(
                new Leaderboard("player1", 10, 6),
                new Leaderboard("player2", 4, 2)
        );
    }

    @Test
    public void givenCachedTopPlayers_whenGetTopPlayers_thenReturnTopPlayers() throws Exception {

        when(leaderboardService.getCachedTop10Players()).thenReturn(topPlayers);

        mockMvc.perform(get("/v1/leaderboard/top"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"username\":\"player1\",\"totalGames\":10,\"totalWins\":6},{\"username\":\"player2\",\"totalGames\":4,\"totalWins\":2 }]"));
    }

    @Test
    public void givenUpdateRequest_whenUpdateLeaderboard_thenReturnUpdatedLeaderboard() throws Exception {

        mockMvc.perform(post("/v1/leaderboard/update")
                .param("username", "testUser")
                .param("isWinner", "true")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Leaderboard successfully updated"));
    }
}
