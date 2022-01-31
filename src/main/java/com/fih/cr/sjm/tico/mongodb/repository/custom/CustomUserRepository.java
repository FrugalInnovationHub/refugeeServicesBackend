package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.User;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;

import java.util.Optional;

public interface CustomUserRepository {
    Optional<User> findUser(String userId, UserTypeEnum userType);

    Boolean userExists(String userId, UserTypeEnum userType);
}
