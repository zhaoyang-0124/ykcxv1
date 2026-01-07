package com.jinzhougang.innvotion.ykcxv1.controller;




import com.jinzhougang.innvotion.ykcxv1.entity.QuestionResult;
import com.jinzhougang.innvotion.ykcxv1.entity.QuizResultResponse;
import com.jinzhougang.innvotion.ykcxv1.entity.QuizSession;
import com.jinzhougang.innvotion.ykcxv1.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    /**
     * 获取题库状态和统计信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("fileStatus", quizService.getFileStatus());
        status.put("questionStats", quizService.getQuestionStats());
        return ResponseEntity.ok(status);
    }

    /**
     * 开始答题 - 从磁盘随机读取85道题
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startQuiz() {
        try {
            QuizSession session = quizService.startQuiz();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", session.getSessionId());
            response.put("questionCount", session.getQuestions().size());
            response.put("message", "答题开始，共" + session.getQuestions().size() + "道题目");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取题目列表
     */
    @GetMapping("/session/{sessionId}/questions")
    public ResponseEntity<QuizSession> getQuestions(@PathVariable String sessionId) {
        QuizSession session = quizService.getSession(sessionId);
        if (session != null) {
            return ResponseEntity.ok(session);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 提交答案
     */
    @PostMapping("/answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> request) {
        try {
            String sessionId = (String) request.get("sessionId");
            Integer questionId = (Integer) request.get("questionId");
            @SuppressWarnings("unchecked")
            List<String> answers = (List<String>) request.get("answers");

            System.out.println("收到答案提交: sessionId=" + sessionId + ", questionId=" + questionId + ", answers=" + answers);

            quizService.submitAnswer(sessionId, questionId, answers);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "答案提交成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    /**
     * 完成答题
     */
    @PostMapping("/finish")
    public ResponseEntity<QuizSession> finishQuiz(@RequestParam String sessionId) {
        try {
            QuizSession session = quizService.finishQuiz(sessionId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取答题会话
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<QuizSession> getSession(@PathVariable String sessionId) {
        QuizSession session = quizService.getSession(sessionId);
        if (session != null) {
            return ResponseEntity.ok(session);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/results")
    public ResponseEntity<QuizResultResponse> getQuizResults(@RequestParam String sessionId) {
        try {
            QuizSession session = quizService.getSession(sessionId);
            if (session == null) {
                return ResponseEntity.badRequest().body(
                        new QuizResultResponse(false, "会话不存在", null, 0)
                );
            }

            List<QuestionResult> results = quizService.calculateResults(session);
            int totalScore = results.stream().mapToInt(QuestionResult::getScore).sum();
            System.out.println(totalScore);
            QuizResultResponse response = new QuizResultResponse(true, null, results, totalScore);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new QuizResultResponse(false, "服务器内部错误", null, 0)
            );
        }
    }
}