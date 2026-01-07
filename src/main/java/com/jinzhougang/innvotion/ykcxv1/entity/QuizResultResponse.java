package com.jinzhougang.innvotion.ykcxv1.entity;

import java.util.List;

public class QuizResultResponse {
    private boolean success;
    private String error;
    private List<QuestionResult> questionResults;
    private int totalScore;

    public QuizResultResponse(boolean success, String error,
                              List<QuestionResult> questionResults, int totalScore) {
        this.success = success;
        this.error = error;
        this.questionResults = questionResults;
        this.totalScore = totalScore;
    }

    // getters and setters...
}