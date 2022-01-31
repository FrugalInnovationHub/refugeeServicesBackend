package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.time.Duration;
import java.util.Base64;
import java.util.List;

public class CustomSessionRepositoryImpl implements CustomSessionRepository {
    private static final int GENERATOR_INIT_LENGTH = 0x42;

    private final MongoTemplate mongoTemplate;

    public CustomSessionRepositoryImpl(
            final MongoTemplate mongoTemplate,
            @Value("${tico.users.sessions.ttl}") final long ttlValue
    ) {
        this.mongoTemplate = mongoTemplate;

        final Index index = new Index();
        index
                .on("createdAt", Direction.ASC)
                .expire(Duration.ofSeconds(ttlValue))
                .named("sessionsTTL");
        this.mongoTemplate.indexOps(Session.class).ensureIndex(index);
    }

    @Override
    public Session newSession(
            final Session session
    ) {
        final String encodedToken = Base64.getEncoder().encodeToString(KeyGenerators.secureRandom(GENERATOR_INIT_LENGTH).generateKey());

        session.setToken(encodedToken);

        return this.mongoTemplate.save(session);
    }

    @Override
    public List<Session> logoutUserSessionEverywhere(
            final String userId,
            final UserTypeEnum userType
    ) {
        final Query query = Query.query(Criteria.where("userType").is(userType).and("userId").is(userId));
        return this.mongoTemplate.findAllAndRemove(query, Session.class);
    }
}
