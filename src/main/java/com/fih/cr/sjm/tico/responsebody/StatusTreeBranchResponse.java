package com.fih.cr.sjm.tico.responsebody;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeQuestion;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonDeserialize
public class StatusTreeBranchResponse {

    private String label;
    private String description;
    private String firstQuestion;

    private Map<String, StatusTreeQuestion> statusTreeQuestions;
}
