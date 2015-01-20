package be.sandervl.util;

public enum CacheName {
    GAME_CACHE("gameCache"),
    RANKING_CACHE("rankingCache");

    private String name;

    CacheName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
