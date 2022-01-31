package com.fih.cr.sjm.tico.requestbody;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize
public class AddUserRequest {
    private String userId;
    private String name;
    private UserTypeEnum userType;
    private String password;
    private String confirmPassword;
}
