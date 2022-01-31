package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;

import java.util.List;

public interface CustomSessionRepository {
    Session newSession(Session session);

    List<Session> logoutUserSessionEverywhere(
            String userId,
            UserTypeEnum userType
    );
}
