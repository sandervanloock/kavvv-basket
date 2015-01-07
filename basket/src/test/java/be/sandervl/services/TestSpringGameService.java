package be.sandervl.services;

import be.sandervl.business.Game;
import be.sandervl.business.Ranking;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:be/sandervl/config/test-application-context.xml")
public class TestSpringGameService {

    @Autowired
    private GameService gameService;

    @Before
    public void setupTest() {
        gameService = new GameServiceImpl();
    }

    @Test
    @Ignore
    public void testCaching() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        Date expectedDate = formatter.parse("31/10/2014 21:15:07");
        Ranking.Type expectedType = null;

        List<Game> result1 = gameService.getSpecificGames(expectedDate, expectedType);
        List<Game> result2 = gameService.getSpecificGames(expectedDate, expectedType);

        Assert.assertEquals(result1.size(), result2.size());
        Assert.assertTrue(result1.size() > 0);
        Assert.assertEquals(result1.get(0), result2.get(0));
    }

}
