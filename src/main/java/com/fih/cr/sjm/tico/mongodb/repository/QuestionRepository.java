package com.fih.cr.sjm.tico.mongodb.repository;

import com.fih.cr.sjm.tico.mongodb.documents.Question;
import com.fih.cr.sjm.tico.mongodb.repository.custom.CustomQuestionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String>, CustomQuestionRepository {
}
