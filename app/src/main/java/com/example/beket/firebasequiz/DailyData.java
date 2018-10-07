package com.example.beket.firebasequiz;

public class DailyData {

    private int gamesPlayed, gamesWon, correctAnswers;
    private float averageTime, playedTime;

    DailyData(){
    }

    DailyData(int gamesPlayed, int gamesWon, int correctAnswers, float averageTime, float playedTime){
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.correctAnswers = correctAnswers;
        this.averageTime = averageTime;
        this.playedTime = playedTime;
    }


    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public float getAverageTime() {
        return averageTime;
    }

    public float getPlayedTime() {
        return playedTime;
    }
}
