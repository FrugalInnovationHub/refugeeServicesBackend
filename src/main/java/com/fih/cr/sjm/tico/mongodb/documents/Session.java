package com.fih.cr.sjm.tico.mongodb.documents;

import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Document(collection = "sessions")
public class Session {

    @Id
    @Getter
    @Setter
    @NonNull
    private String token;

    @Field
    @Getter
    @Setter
    @NonNull
    private String id;

    @Field
    @Getter
    @Setter
    @NonNull
    private String userId;

    @Field
    @Getter
    @Setter
    @NonNull
    private UserTypeEnum userType;

    @Field
    @NonNull
    private Date createdAt;

    @Builder
    private static Session of(
            final String token,
            final String id,
            final String userId,
            final UserTypeEnum userType
    ) {
        Session session = new Session();

        session.token = token;
        session.id = id;
        session.userId = userId;
        session.userType = userType;
        session.createdAt = new Date();
        return session;
    }

    public Date getCreatedAt() {
        return (Date) this.createdAt.clone();
    }

}
