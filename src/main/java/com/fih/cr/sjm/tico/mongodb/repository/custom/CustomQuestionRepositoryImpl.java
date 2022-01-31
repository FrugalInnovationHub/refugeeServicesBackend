package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.Question;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

import java.util.List;

public class CustomQuestionRepositoryImpl implements CustomQuestionRepository {
    private final MongoTemplate mongoTemplate;

    public CustomQuestionRepositoryImpl(
            final MongoTemplate mongoTemplate
    ) {
        this.mongoTemplate = mongoTemplate;

        final TextIndexDefinition textIndexDefinition = TextIndexDefinition
                .builder()
                .onField("prompt.value")
                .onField("explanation.value")
                .onField("options.a")
                .onField("options.b")
                .onField("options.c")
                .onField("options.d")
                .named("questions.keywords")
                .build();

        this.mongoTemplate.indexOps(Question.class).ensureIndex(textIndexDefinition);
    }

    @Override
    public List<Question> findQuestionsByKeyword(
            final String keywords
    ) {
        final TextCriteria textCriteria = TextCriteria
                .forDefaultLanguage()
                .caseSensitive(false)
                .diacriticSensitive(true)
                .matching(keywords);

        return mongoTemplate.find(TextQuery.queryText(textCriteria), Question.class);
    }

    @Override
    public List<Question> sampleQuestions(
            final long sampleSize
    ) {
        final AggregationResults<Question> aggregate = mongoTemplate.aggregate(
                TypedAggregation.newAggregation(
                        Aggregation.sample(sampleSize)
                ),
                Question.class,
                Question.class
        );

        return aggregate.getMappedResults();
    }
}
