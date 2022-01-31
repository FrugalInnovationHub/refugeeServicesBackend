package com.fih.cr.sjm.tico.requestbody;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginUserRequest {
    private String userId;
    private String password;
}
