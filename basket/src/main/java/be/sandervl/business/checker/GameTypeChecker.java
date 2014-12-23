package be.sandervl.business.checker;

import be.sandervl.business.Game;
import be.sandervl.business.Ranking;

public class GameTypeChecker implements Checker<Game> {

    private final Ranking.Type type;

    public GameTypeChecker(Ranking.Type type){
        this.type = type;
    }
    @Override
    public boolean check(Game obj) {
        return obj.getType().equals(type);
    }
}
