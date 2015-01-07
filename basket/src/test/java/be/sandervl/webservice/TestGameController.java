package be.sandervl.webservice;

import be.sandervl.services.GameService;
import be.sandervl.services.GameServiceImpl;
import be.sandervl.services.RankingService;
import be.sandervl.services.RankingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath*:be/sandervl/config/test-application-context.xml")
public class TestGameController {

    @Autowired
    private GameService gameService;

    MockMvc mockMvc;

    @Before
    public void init() {
        GameController gameController = new GameController();
        gameService = new GameServiceImpl();
        setField(gameController, "gameService", gameService);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
    }

    @Test
    public void getAllGamesOfDateaAndType() throws Exception {
        mockMvc.perform(get("/game?date=17-12-2014&type=heren+3b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].team1").value("Grand Cru Kessel"))
                .andExpect(jsonPath("$[0].team2").value("Klavermannen"));
    }

}
