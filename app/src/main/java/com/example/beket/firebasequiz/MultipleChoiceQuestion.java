package com.example.beket.firebasequiz;

import java.util.List;

public class MultipleChoiceQuestion {

    private String question, correctAnswer;
    private List<String> choiceList;

    MultipleChoiceQuestion(String question, List<String> choiceList, String correctAnswer) {
        this.question = question;
        this.choiceList = choiceList;
        this.correctAnswer = correctAnswer;
    }

    String getQuestion() {
        return question;
    }

    List<String> getChoiceOne() {
        return choiceList;
    }

    String getCorrectAnswer() {
        return correctAnswer;
    }
}
