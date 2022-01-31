package com.fih.cr.sjm.tico.mongodb.repository;

import com.fih.cr.sjm.tico.mongodb.documents.User;
import com.fih.cr.sjm.tico.mongodb.repository.custom.CustomUserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>, CustomUserRepository {
}
