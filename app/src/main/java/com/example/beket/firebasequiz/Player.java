package com.example.beket.firebasequiz;

public class Player {

    private String userName, uniqueKey, userImageUri;
    private boolean online;
    private int gamesWon;

    Player() {

    }

    public Player(String userName, String uniqueKey, String userImageUri, boolean online, int gamesWon) {
        this.uniqueKey = uniqueKey;
        this.userName = userName;
        this.online = online;
        this.userImageUri = userImageUri;
        this.gamesWon = gamesWon;
    }

    public String getUserName() {
        return userName;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUserImageUri() {
        return userImageUri;
    }

    public int getGamesWon() {
        return gamesWon;
    }
}
