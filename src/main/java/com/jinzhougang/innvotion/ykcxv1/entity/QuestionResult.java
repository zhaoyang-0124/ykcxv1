package com.jinzhougang.innvotion.ykcxv1.entity;

import java.util.List;

public class QuestionResult {
    private Integer questionId;
    private String questionContent;
    private Integer questionType;
    private List<String> userAnswers;
    private List<String> correctAnswers;
    private boolean isCorrect;
    private Integer score;

    // 构造函数
    public QuestionResult() {}

    public QuestionResult(Integer questionId, String questionContent, Integer questionType,
                          List<String> userAnswers, List<String> correctAnswers,
                          boolean isCorrect, Integer score) {
        this.questionId = questionId;
        this.questionContent = questionContent;
        this.questionType = questionType;
        this.userAnswers = userAnswers;
        this.correctAnswers = correctAnswers;
        this.isCorrect = isCorrect;
        this.score = score;
    }

    // Getter和Setter方法
    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(List<String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}