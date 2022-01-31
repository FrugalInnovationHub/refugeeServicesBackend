package com.fih.cr.sjm.tico.requestbody;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize
public class ApproveUserRequest {
    private String userId;
    private UserTypeEnum userType;
}
