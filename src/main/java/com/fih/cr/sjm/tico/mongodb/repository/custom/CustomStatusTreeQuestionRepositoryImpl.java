package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeQuestion;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomStatusTreeQuestionRepositoryImpl implements CustomStatusTreeQuestionRepository {
    private final MongoTemplate mongoTemplate;

    public CustomStatusTreeQuestionRepositoryImpl(
            final MongoTemplate mongoTemplate
    ) {
        this.mongoTemplate = mongoTemplate;

        this.mongoTemplate.indexOps(StatusTreeQuestion.class)
                .ensureIndex(new Index().named("status_tree.branchId.index").on("branchId", Direction.ASC));
    }

    @Override
    public Map<String, StatusTreeQuestion> findByBranchId(
            final String branchId
    ) {
        final List<StatusTreeQuestion> statusTreeQuestions = this.mongoTemplate
                .find(Query.query(Criteria.where("branchId").is(branchId)), StatusTreeQuestion.class);

        return statusTreeQuestions
                .stream()
                .collect(Collectors.toMap(
                        StatusTreeQuestion::getId,
                        Function.identity(),
                        (existing, replacement) -> replacement)
                );
    }

    @Override
    public Boolean deleteByBranchId(
            final String branchId
    ) {
        final DeleteResult deleteResult = this.mongoTemplate
                .remove(Query.query(Criteria.where("branchId").is(branchId)), StatusTreeQuestion.class);

        return deleteResult.wasAcknowledged();
    }
}
