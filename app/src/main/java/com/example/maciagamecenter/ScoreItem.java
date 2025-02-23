package com.example.maciagamecenter;

public class ScoreItem {
    private String playerName;
    private int score;

    public ScoreItem(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }
}