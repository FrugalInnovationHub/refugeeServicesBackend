package com.fih.cr.sjm.tico.mongodb.repository;

import com.fih.cr.sjm.tico.mongodb.documents.Config;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends MongoRepository<Config, String> {
}
