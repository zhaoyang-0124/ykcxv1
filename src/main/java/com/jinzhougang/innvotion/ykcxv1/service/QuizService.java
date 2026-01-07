package com.jinzhougang.innvotion.ykcxv1.service;


import com.jinzhougang.innvotion.ykcxv1.entity.Question;
import com.jinzhougang.innvotion.ykcxv1.entity.QuestionResult;
import com.jinzhougang.innvotion.ykcxv1.entity.QuizSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class QuizService {

    @Autowired
    private WordQuestionLoader questionLoader;

    private Map<String, QuizSession> sessions = new HashMap<>();

    /**
     * 开始答题 - 从磁盘随机读取85道题
     */
    public QuizSession startQuiz() throws Exception {
        // 检查文件是否存在
        if (!questionLoader.isFileExists()) {
            throw new RuntimeException("题库文件不存在，请检查文件路径配置");
        }

        // 从磁盘随机读取85道题
        List<Question> selectedQuestions = questionLoader.loadRandomQuestions();
        if (selectedQuestions.isEmpty()) {
            throw new RuntimeException("题库中没有找到题目，请检查Word文件格式");
        }

        // 创建答题会话
        String sessionId = UUID.randomUUID().toString();
        QuizSession session = new QuizSession();
        session.setSessionId(sessionId);
        session.setQuestions(selectedQuestions);
        session.setUserAnswers(new HashMap<>());
        session.setCompleted(false);

        sessions.put(sessionId, session);

        System.out.println("用户开始答题，会话ID: " + sessionId + "，题目数量: " + selectedQuestions.size());

        return session;
    }

    public void submitAnswer(String sessionId, Integer questionId, List<String> answers) {
        QuizSession session = sessions.get(sessionId);
        if (session != null) {
            session.getUserAnswers().put(questionId, answers);
        }
    }

    public QuizSession finishQuiz(String sessionId) {
        QuizSession session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("答题会话不存在");
        }

        int totalScore = 0;
        for (Question question : session.getQuestions()) {
            List<String> userAnswers = session.getUserAnswers().get(question.getId());
            if (userAnswers != null) {
                int score = calculateScore(question, userAnswers);
                totalScore += score;
            }
        }

        session.setTotalScore(totalScore);
        session.setCompleted(true);

        System.out.println("用户完成答题，会话ID: " + sessionId + "，得分: " + totalScore);

        return session;
    }

    private int calculateScore(Question question, List<String> userAnswers) {
        List<String> correctAnswers = question.getCorrectAnswers();

        if (question.getType() == 1) {
            // 单选题：完全正确得满分
            return userAnswers.equals(correctAnswers) ? question.getScore() : 0;
        } else if (question.getType() == 2) {
            // 多选题：选多不得分，选少按比例得分
            if (userAnswers.size() > correctAnswers.size()) {
                return 0; // 选多了不得分
            }

            int correctCount = 0;
            for (String answer : userAnswers) {
                if (correctAnswers.contains(answer)) {
                    correctCount++;
                }
            }

            if (correctCount == 0) {
                return 0;
            }
            return (int) Math.round((double) correctCount / correctAnswers.size() * question.getScore());
        } else {
            // 判断题：完全正确得满分
            return userAnswers.equals(correctAnswers) ? question.getScore() : 0;
        }
    }

    // 计算答题结果
    public List<QuestionResult> calculateResults(QuizSession session) {
        List<QuestionResult> results = new ArrayList<>();

        for (Question question : session.getQuestions()) {
            List<String> userAnswers = session.getUserAnswers().getOrDefault(question.getId(), new ArrayList<>());
            List<String> correctAnswers = question.getCorrectAnswers();

            boolean isCorrect = compareAnswers(userAnswers, correctAnswers, question.getType());
            int score = isCorrect ? question.getScore() : 0;

            QuestionResult result = new QuestionResult(
                    question.getId(),
                    question.getContent(),
                    question.getType(),
                    userAnswers,
                    correctAnswers,
                    isCorrect,
                    score
            );

            results.add(result);
        }

        return results;
    }

    // 比较答案是否正确
    private boolean compareAnswers(List<String> userAnswers, List<String> correctAnswers, int questionType) {
        if (userAnswers == null || correctAnswers == null) {
            return false;
        }

        // 对答案进行排序以确保比较的准确性
        Collections.sort(userAnswers);
        Collections.sort(correctAnswers);

        switch (questionType) {
            case 1: // 单选题
            case 3: // 判断题
                return userAnswers.equals(correctAnswers);
            case 2: // 多选题
                return userAnswers.equals(correctAnswers);
            default:
                return false;
        }
    }

    public QuizSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * 获取题库统计信息
     */
    public Map<String, Object> getQuestionStats() {
        try {
            return questionLoader.getQuestionStats();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * 检查题库文件状态
     */
    public Map<String, Object> getFileStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("fileExists", questionLoader.isFileExists());
        status.put("fileInfo", questionLoader.getFileInfo());
        return status;
    }
}