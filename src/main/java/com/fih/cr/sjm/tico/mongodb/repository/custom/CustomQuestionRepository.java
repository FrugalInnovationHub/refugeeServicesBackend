package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.Question;

import java.util.List;

public interface CustomQuestionRepository {

    List<Question> findQuestionsByKeyword(
            String keywords
    );

    List<Question> sampleQuestions(
            long sampleSize
    );
}
