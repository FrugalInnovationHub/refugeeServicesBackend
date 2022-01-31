package com.fih.cr.sjm.tico.controller;

import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.mongodb.documents.Config;
import com.fih.cr.sjm.tico.mongodb.documents.Question;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.service.QuestionService;
import com.fih.cr.sjm.tico.utilities.RolesUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final RolesUtil rolesUtil;

    public QuestionController(
            final QuestionService questionService,
            final RolesUtil rolesUtil
    ) {
        this.questionService = questionService;
        this.rolesUtil = rolesUtil;
    }

    @GetMapping
    public ResponseEntity<List<Question>> findAllQuestions(
    ) {
        final List<Question> allQuestions = questionService.findAllQuestions();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allQuestions);
    }

    @GetMapping(value = "/mockTest")
    public ResponseEntity<List<Question>> getMockTestQuestions(
            @RequestParam(defaultValue = "20") final int sampleSize
    ) {
        final List<Question> mockTestQuestions = questionService.getMockTestQuestions(sampleSize);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mockTestQuestions);
    }

    @GetMapping(value = "/{_id}")
    public ResponseEntity<Object> findQuestionById(
            @PathVariable("_id") final String id
    ) {
        try {
            final Question questionById = questionService.findQuestionById(id);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(questionById);
        } catch (TicoException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getResponseBody());
        }

    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Question> saveQuestion(
            @RequestBody final Question question
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final Question createdQuestion = questionService.saveQuestion(question);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdQuestion);
    }

    @PostMapping(value = "/bulk", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Question>> saveQuestions(
            @RequestBody final List<Question> questions
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final List<Question> createdQuestions = questionService.saveQuestions(questions);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdQuestions);
    }

    @PutMapping(value = "/{_id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updateQuestion(
            @PathVariable("_id") final String id,
            @RequestBody final Question question
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final Question updatedQuestion;
        try {
            updatedQuestion = questionService.updateQuestion(id, question);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updatedQuestion);
        } catch (TicoException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

    }

    @DeleteMapping(value = "/{_id}", produces = "application/json")
    public ResponseEntity<Object> deleteQuestion(
            @PathVariable("_id") final String id
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.noContent().build();
        } catch (TicoException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Question>> findQuestionsByKeywords(
            @RequestParam @NotBlank(message = "keywords cannot be blank!") final String keywords
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final List<Question> questionsByKeywords = questionService.findQuestionsByKeywords(keywords);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(questionsByKeywords);
    }

    @GetMapping(path = "/mockTest/config")
    public ResponseEntity<List<Config>> getMockTestConfigs(
    ) {
        final List<Config> mockTestConfigs = this.questionService.getMockTestConfigs();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mockTestConfigs);
    }

    @PutMapping(path = "/mockTest/config")
    public ResponseEntity<List<Config>> getMockTestConfigs(
            @RequestBody final List<Config> mockTestConfigs
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final List<Config> updatedMockTestConfigs = this.questionService.updateMockTestConfigs(mockTestConfigs);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedMockTestConfigs);
    }
}
