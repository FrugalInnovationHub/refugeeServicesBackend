package com.fih.cr.sjm.tico.mongodb.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
@Builder
@Data
public class User extends AbstractDocument {
    @Field
    private String userId;

    @Field
    private String name;

    @Field
    private UserTypeEnum userType;

    @JsonInclude(Include.NON_NULL)
    @Field
    private String password;

    @Field
    private boolean isActive;
}
