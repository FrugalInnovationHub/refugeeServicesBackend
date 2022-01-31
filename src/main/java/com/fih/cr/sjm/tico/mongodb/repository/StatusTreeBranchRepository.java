package com.fih.cr.sjm.tico.mongodb.repository;

import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeBranch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusTreeBranchRepository extends MongoRepository<StatusTreeBranch, String> {
}
