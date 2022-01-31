package com.fih.cr.sjm.tico.mongodb.repository;

import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeQuestion;
import com.fih.cr.sjm.tico.mongodb.repository.custom.CustomStatusTreeQuestionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusTreeQuestionRepository extends MongoRepository<StatusTreeQuestion, String>, CustomStatusTreeQuestionRepository {
}
