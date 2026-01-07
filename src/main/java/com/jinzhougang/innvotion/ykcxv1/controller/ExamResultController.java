package com.jinzhougang.innvotion.ykcxv1.controller;



import com.jinzhougang.innvotion.ykcxv1.entity.ExamResult;
import com.jinzhougang.innvotion.ykcxv1.entity.FinishRequest;
import com.jinzhougang.innvotion.ykcxv1.entity.QuestionResult;
import com.jinzhougang.innvotion.ykcxv1.entity.QuizSession;
import com.jinzhougang.innvotion.ykcxv1.service.ExamResultServiceImpl;
import com.jinzhougang.innvotion.ykcxv1.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ExamResultController {
    private ExamResultServiceImpl service;

    public ExamResultController(ExamResultServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/api/exam/results")
    public ResponseEntity<ExamResult> saveExamResult(@RequestBody FinishRequest request
    ) {
        String examNumber = request.getExamNumber();
        Integer score = request.getScore();
        System.out.println(examNumber+"////"+score);
        if (score == null || score < 0 || score > 100) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            ExamResult result = service.saveResult(examNumber, score);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}