package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeQuestion;

import java.util.Map;

public interface CustomStatusTreeQuestionRepository {
    Map<String, StatusTreeQuestion> findByBranchId(String branchId);

    Boolean deleteByBranchId(String branchId);
}
