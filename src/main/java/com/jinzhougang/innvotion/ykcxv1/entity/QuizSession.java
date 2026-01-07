package com.jinzhougang.innvotion.ykcxv1.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class QuizSession {
    private String sessionId;
    private List<Question> questions;
    private Map<Integer, List<String>> userAnswers; // 题目ID -> 用户答案
    private Integer totalScore;
    private Boolean completed;
//业务方法
    public void addAnswer(Integer questionId, List<String> answers) {
        this.userAnswers.put(questionId, answers);
    }

    public List<String> getAnswer(Integer questionId) {
        return this.userAnswers.getOrDefault(questionId, new ArrayList<>());
    }

    public void calculateTotalScore() {
        if (questions == null || userAnswers == null) {
            this.totalScore = 0;
            return;
        }

        int score = 0;
        for (Question question : questions) {
            List<String> userAns = userAnswers.getOrDefault(question.getId(), new ArrayList<>());
            if (isAnswerCorrect(userAns, question.getCorrectAnswers(), question.getType())) {
                score += question.getScore();
            }
        }
        this.totalScore = score;
    }

    private boolean isAnswerCorrect(List<String> userAnswers, List<String> correctAnswers, Integer questionType) {
        if (userAnswers == null || correctAnswers == null) {
            return false;
        }

        // 对答案进行排序以确保比较的准确性
        List<String> sortedUserAnswers = new ArrayList<>(userAnswers);
        List<String> sortedCorrectAnswers = new ArrayList<>(correctAnswers);
        Collections.sort(sortedUserAnswers);
        Collections.sort(sortedCorrectAnswers);

        switch (questionType) {
            case 1: // 单选题
            case 3: // 判断题
                return sortedUserAnswers.equals(sortedCorrectAnswers);
            case 2: // 多选题
                return sortedUserAnswers.equals(sortedCorrectAnswers);
            default:
                return false;
        }
    }

    public List<QuestionResult> getQuestionResults() {
        List<QuestionResult> results = new ArrayList<>();
        if (questions == null) {
            return results;
        }

        for (Question question : questions) {
            List<String> userAns = userAnswers.getOrDefault(question.getId(), new ArrayList<>());
            List<String> correctAns = question.getCorrectAnswers();
            boolean isCorrect = isAnswerCorrect(userAns, correctAns, question.getType());
            Integer score = isCorrect ? question.getScore() : 0;

            QuestionResult result = new QuestionResult(
                    question.getId(),
                    question.getContent(),
                    question.getType(),
                    userAns,
                    correctAns,
                    isCorrect,
                    score
            );
            results.add(result);
        }

        return results;
    }
}
