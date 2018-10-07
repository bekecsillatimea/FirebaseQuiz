package com.example.beket.firebasequiz;

import java.util.List;

public class SwipeQuestion {

    private String question, correctAnswer;
    private List<String> choiceList;
    private List<String> toSwipeList;

    SwipeQuestion(String question, List<String> choiceList, List<String> toSwipeList, String correctAnswer) {
        this.question = question;
        this.choiceList = choiceList;
        this.toSwipeList = toSwipeList;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getChoiceList() {
        return choiceList;
    }

    public List<String> getToSwipeList() {
        return toSwipeList;
    }
}
