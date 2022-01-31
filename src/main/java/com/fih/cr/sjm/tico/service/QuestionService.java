package com.fih.cr.sjm.tico.service;

import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.mongodb.documents.Config;
import com.fih.cr.sjm.tico.mongodb.documents.Question;
import com.fih.cr.sjm.tico.mongodb.repository.QuestionRepository;
import com.fih.cr.sjm.tico.utilities.MockTestConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ConfigService configService;

    public QuestionService(
            final QuestionRepository questionRepository,
            final ConfigService configService
    ) {
        this.questionRepository = questionRepository;
        this.configService = configService;
    }

    public Question saveQuestion(
            final Question question
    ) {
        return this.questionRepository.save(question);
    }

    public List<Question> saveQuestions(
            final List<Question> questions
    ) {
        return this.questionRepository.saveAll(questions);
    }

    public Question updateQuestion(
            final String id,
            final Question question
    ) throws TicoException {
        if (this.questionRepository.existsById(id)) {
            question.setId(id);
            return this.questionRepository.save(question);
        } else {
            throw new TicoException("Question does not exist!");
        }
    }

    public List<Question> findAllQuestions() {
        return this.questionRepository.findAll();
    }

    public List<Question> getMockTestQuestions(
            final int sampleSize
    ) {
        return this.questionRepository.sampleQuestions(sampleSize);
    }

    public Question findQuestionById(
            final String id
    ) throws TicoException {
        final Optional<Question> questionById = this.questionRepository.findById(id);
        if (questionById.isPresent()) {
            return questionById.get();
        } else {
            throw new TicoException("Question does not exist!");
        }
    }

    public List<Question> findQuestionsByKeywords(
            final String keywords
    ) {
        return this.questionRepository.findQuestionsByKeyword(keywords);
    }

    public void deleteQuestion(
            final String id
    ) throws TicoException {
        if (this.questionRepository.existsById(id)) {
            this.questionRepository.deleteById(id);
        } else {
            throw new TicoException("Question does not exist!");
        }
    }

    public List<Config> getMockTestConfigs() {
        final List<Config> collect = MockTestConfigUtil.CONFIG_LIST.parallelStream()
                .map(config -> this.configService.getConfig(config)
                        .orElse(Config.builder().key(config).build()))
                .collect(Collectors.toList());
        collect.add(Config.builder()
                .key(MockTestConfigUtil.MAX_SIZE)
                .value(this.questionRepository.count())
                .build());
        return collect;
    }

    public List<Config> updateMockTestConfigs(
            final List<Config> mockTestConfigs
    ) {
        return this.configService.addConfigList(
                MockTestConfigUtil.sanitizeUpdateRequest(mockTestConfigs).stream()
                        .filter(config -> MockTestConfigUtil.CONFIG_LIST.contains(config.getKey()))
                        .collect(Collectors.toList())
        );
    }
}
