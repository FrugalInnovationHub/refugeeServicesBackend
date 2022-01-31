package com.fih.cr.sjm.tico.service;

import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeBranch;
import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeQuestion;
import com.fih.cr.sjm.tico.mongodb.repository.StatusTreeBranchRepository;
import com.fih.cr.sjm.tico.mongodb.repository.StatusTreeQuestionRepository;
import com.fih.cr.sjm.tico.mongodb.structures.TypeEnum;
import com.fih.cr.sjm.tico.mongodb.structures.TypeValue;
import com.fih.cr.sjm.tico.requestbody.AddStatusTreeQuestionRequest;
import com.fih.cr.sjm.tico.responsebody.StatusTreeBranchResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class StatusTreeService {
    private final StatusTreeBranchRepository statusTreeBranchRepository;
    private final StatusTreeQuestionRepository statusTreeQuestionRepository;

    public StatusTreeService(
            final StatusTreeBranchRepository statusTreeBranchRepository,
            final StatusTreeQuestionRepository statusTreeQuestionRepository
    ) {
        this.statusTreeBranchRepository = statusTreeBranchRepository;
        this.statusTreeQuestionRepository = statusTreeQuestionRepository;
    }

    public List<StatusTreeBranch> getStatusTreeBranches(

    ) {
        return this.statusTreeBranchRepository.findAll();
    }

    public StatusTreeBranch addNewStatusTreeBranch(
            final StatusTreeBranch statusTreeBranch
    ) {
        return this.statusTreeBranchRepository.insert(statusTreeBranch);
    }

    public void clearStatusTreeBranch(
            final String branchId
    ) throws TicoException {
        if (!this.statusTreeQuestionRepository.deleteByBranchId(branchId)) {
            throw new TicoException("Error deleting questions!");
        }
    }

    public void deleteStatusTreeBranch(
            final String branchId
    ) throws TicoException {
        this.clearStatusTreeBranch(branchId);
        this.statusTreeBranchRepository.deleteById(branchId);
    }

    public StatusTreeQuestion addNewStatusTreeQuestion(
            final String branchId,
            final String parentQuestionId,
            final String response,
            final AddStatusTreeQuestionRequest addStatusTreeQuestionRequest
    ) throws TicoException {
        return this.statusTreeQuestionRepository.findById(parentQuestionId).map(prevQuestionFromDb -> {
            final StatusTreeQuestion statusTreeQuestion = StatusTreeQuestion
                    .builder()
                    .branchId(branchId)
                    .question(addStatusTreeQuestionRequest.getQuestion())
                    .statusTreeResponseOptions(addStatusTreeQuestionRequest.getStatusTreeResponseOptions())
                    .build();

            final StatusTreeQuestion newTreeQuestion = this.statusTreeQuestionRepository.insert(statusTreeQuestion);

            final HashMap<String, ArrayList<TypeValue>> statusTreeResponseOptions = prevQuestionFromDb.getStatusTreeResponseOptions();
            final TypeValue typeValue = TypeValue.builder().type(TypeEnum.ID).value(newTreeQuestion.getId()).build();

            final ArrayList<TypeValue> responseList = statusTreeResponseOptions.getOrDefault(response, new ArrayList<>());
            responseList.add(typeValue);

            statusTreeResponseOptions.put(response, responseList);
            prevQuestionFromDb.setStatusTreeResponseOptions(statusTreeResponseOptions);

            this.statusTreeQuestionRepository.save(prevQuestionFromDb);

            return newTreeQuestion;
        }).orElseThrow(() -> new TicoException("Parent not found!"));
    }

    public StatusTreeQuestion addNewStatusTreeMessage(
            final String questionId,
            final String response,
            final String message
    ) throws TicoException {
        return this.statusTreeQuestionRepository.findById(questionId).map(questionFromDb -> {
            final HashMap<String, ArrayList<TypeValue>> statusTreeResponseOptions = questionFromDb.getStatusTreeResponseOptions();
            final TypeValue typeValue = TypeValue.builder().type(TypeEnum.TXT).value(message).build();

            final ArrayList<TypeValue> responseList = statusTreeResponseOptions.getOrDefault(response, new ArrayList<>());
            responseList.add(typeValue);

            statusTreeResponseOptions.put(response, responseList);
            questionFromDb.setStatusTreeResponseOptions(statusTreeResponseOptions);

            return this.statusTreeQuestionRepository.save(questionFromDb);
        }).orElseThrow(() -> new TicoException("Parent not found!"));
    }

    public StatusTreeQuestion linkStatusTreeQuestions(
            final String parentQuestionId,
            final String response,
            final String questionId
    ) throws TicoException {
        this.statusTreeQuestionRepository.findById(questionId).orElseThrow(() -> new TicoException("Question Not found!"));
        return this.statusTreeQuestionRepository.findById(parentQuestionId).map(questionFromDb -> {
            final HashMap<String, ArrayList<TypeValue>> statusTreeResponseOptions = questionFromDb.getStatusTreeResponseOptions();
            final TypeValue typeValue = TypeValue.builder().type(TypeEnum.ID).value(questionId).build();

            final ArrayList<TypeValue> responseList = statusTreeResponseOptions.getOrDefault(response, new ArrayList<>());
            responseList.add(typeValue);

            statusTreeResponseOptions.put(response, responseList);
            questionFromDb.setStatusTreeResponseOptions(statusTreeResponseOptions);

            return this.statusTreeQuestionRepository.save(questionFromDb);
        }).orElseThrow(() -> new TicoException("Parent not found!"));
    }

    public StatusTreeQuestion addFirstStatusTreeQuestion(
            final String branchId,
            final AddStatusTreeQuestionRequest addStatusTreeQuestionRequest
    ) throws TicoException {
        return this.statusTreeBranchRepository.findById(branchId).map(statusTreeBranch -> {
            final StatusTreeQuestion statusTreeQuestion = StatusTreeQuestion
                    .builder()
                    .branchId(branchId)
                    .question(addStatusTreeQuestionRequest.getQuestion())
                    .statusTreeResponseOptions(addStatusTreeQuestionRequest.getStatusTreeResponseOptions())
                    .build();

            final StatusTreeQuestion newTreeQuestion = this.statusTreeQuestionRepository.save(statusTreeQuestion);
            statusTreeBranch.setFirstQuestion(newTreeQuestion.getId());

            this.statusTreeBranchRepository.save(statusTreeBranch);

            return newTreeQuestion;
        }).orElseThrow(() -> new TicoException("Parent not found!"));
    }

    public StatusTreeBranchResponse getStatusTreeBranch(
            final String branchId
    ) throws TicoException {
        return this.statusTreeBranchRepository.findById(branchId)
                .map(statusTreeBranch -> StatusTreeBranchResponse.builder()
                        .label(statusTreeBranch.getLabel())
                        .description(statusTreeBranch.getDescription())
                        .firstQuestion(statusTreeBranch.getFirstQuestion())
                        .statusTreeQuestions(this.statusTreeQuestionRepository.findByBranchId(branchId))
                        .build()
                ).orElseThrow(() -> new TicoException("Branch not found!"));
    }
}
