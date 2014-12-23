package be.sandervl.services;

import be.sandervl.business.Game;
import be.sandervl.business.Ranking;

import java.util.Date;
import java.util.List;

public interface GameService {
    List<Game> getGames();

    List<Game> getSpecificGames(Date date, Ranking.Type type);
}
