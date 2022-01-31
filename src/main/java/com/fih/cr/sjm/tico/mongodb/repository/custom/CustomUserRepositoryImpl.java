package com.fih.cr.sjm.tico.mongodb.repository.custom;

import com.fih.cr.sjm.tico.mongodb.documents.User;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

public class CustomUserRepositoryImpl implements CustomUserRepository  {
    private final MongoTemplate mongoTemplate;

    public CustomUserRepositoryImpl(
            final MongoTemplate mongoTemplate
    ) {
        this.mongoTemplate = mongoTemplate;

        this.mongoTemplate.indexOps(User.class)
                .ensureIndex(new Index().named("user.userId.index").on("userId", Direction.ASC));
        this.mongoTemplate.indexOps(User.class)
                .ensureIndex(new Index().named("user.userType.index").on("userType", Direction.ASC));
    }

    @Override
    public Optional<User> findUser(
            final String userId,
            final UserTypeEnum userType
    ) {
        final Query query = Query.query(Criteria.where("userType").is(userType).and("userId").is(userId));
        final User foundUser = this.mongoTemplate.findOne(query, User.class);

        return Optional.ofNullable(foundUser);
    }

    @Override
    public Boolean userExists(
            final String userId,
            final UserTypeEnum userType
    ) {
        final Query query = Query.query(Criteria.where("userType").is(userType).and("userId").is(userId));
        return this.mongoTemplate.exists(query, User.class);
    }
}
