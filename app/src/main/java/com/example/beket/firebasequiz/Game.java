package com.example.beket.firebasequiz;

public class Game {

    private int correctAnswers, points;
    private float timePlayed, averageAnswerTime;

    Game(){
    }

    Game(int correctAnswers, int points, float timePlayed, float averageAnswerTime){
        this.correctAnswers = correctAnswers;
        this.points = points;
        this.timePlayed = timePlayed;
        this.averageAnswerTime = averageAnswerTime;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getPoints() {
        return points;
    }

    public float getTimePlayed() {
        return timePlayed;
    }

    public float getAverageAnswerTime() {
        return averageAnswerTime;
    }
}
