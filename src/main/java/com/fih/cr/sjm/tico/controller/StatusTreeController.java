package com.fih.cr.sjm.tico.controller;

import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeBranch;
import com.fih.cr.sjm.tico.mongodb.documents.StatusTreeQuestion;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.requestbody.AddStatusTreeQuestionRequest;
import com.fih.cr.sjm.tico.responsebody.StatusTreeBranchResponse;
import com.fih.cr.sjm.tico.service.StatusTreeService;
import com.fih.cr.sjm.tico.utilities.RolesUtil;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/statusTree")
public class StatusTreeController {
    private final StatusTreeService statusTreeService;
    private final RolesUtil rolesUtil;

    public StatusTreeController(
            final StatusTreeService statusTreeService,
            final RolesUtil rolesUtil
    ) {
        this.statusTreeService = statusTreeService;
        this.rolesUtil = rolesUtil;
    }

    @SneakyThrows
    @GetMapping()
    public ResponseEntity getStatusTreeBranches() {
        final List<StatusTreeBranch> statusTreeBranches = this.statusTreeService.getStatusTreeBranches();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statusTreeBranches);
    }

    @SneakyThrows
    @PostMapping("/branch")
    public ResponseEntity newBranch(
            @RequestBody final StatusTreeBranch statusTreeBranch
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final StatusTreeBranch addedStatusTreeBranch = this.statusTreeService.addNewStatusTreeBranch(statusTreeBranch);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(addedStatusTreeBranch);
    }

    @SneakyThrows
    @GetMapping("/branch/{branchId}")
    public ResponseEntity getBranchById(
            @PathVariable("branchId") final String branchId
    ) {
        final StatusTreeBranchResponse statusTreeBranch = this.statusTreeService.getStatusTreeBranch(branchId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statusTreeBranch);
    }

    @SneakyThrows
    @DeleteMapping("/branch/{branchId}/clear")
    public ResponseEntity clearBranchById(
            @PathVariable("branchId") final String branchId
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        this.statusTreeService.clearStatusTreeBranch(branchId);

        return ResponseEntity
                .noContent().build();
    }

    @SneakyThrows
    @DeleteMapping("/branch/{branchId}")
    public ResponseEntity deleteBranchById(
            @PathVariable("branchId") final String branchId
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        this.statusTreeService.deleteStatusTreeBranch(branchId);

        return ResponseEntity
                .noContent().build();
    }

    @SneakyThrows
    @PostMapping("/branch/{branchId}/firstQuestion")
    public ResponseEntity addFirstQuestion(
            @PathVariable("branchId") final String branchId,
            @RequestBody final AddStatusTreeQuestionRequest statusTreeQuestion
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final StatusTreeQuestion addedStatusTreeQuestion = this.statusTreeService
                .addFirstStatusTreeQuestion(branchId, statusTreeQuestion);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(addedStatusTreeQuestion);
    }

    @SneakyThrows
    @PostMapping("/branch/{branchId}/question/{questionId}/{response}/question")
    public ResponseEntity addQuestion(
            @PathVariable("branchId") final String branchId,
            @PathVariable("questionId") final String parentQuestionId,
            @PathVariable("response") final String response,
            @RequestBody final AddStatusTreeQuestionRequest statusTreeQuestion
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final StatusTreeQuestion addedStatusTreeQuestion = this.statusTreeService
                .addNewStatusTreeQuestion(branchId, parentQuestionId, response, statusTreeQuestion);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(addedStatusTreeQuestion);
    }

    @SneakyThrows
    @PutMapping("/branch/{branchId}/question/{parentQuestionId}/{response}/{questionId}")
    public ResponseEntity addQuestionById(
            @PathVariable("branchId") final String branchId,
            @PathVariable("parentQuestionId") final String parentQuestionId,
            @PathVariable("response") final String response,
            @PathVariable("questionId") final String questionId
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final StatusTreeQuestion addedStatusTreeQuestion = this.statusTreeService
                .linkStatusTreeQuestions(parentQuestionId, response, questionId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(addedStatusTreeQuestion);
    }

    @SneakyThrows
    @PostMapping("/branch/{branchId}/question/{questionId}/{response}/message")
    public ResponseEntity addMessage(
            @PathVariable("branchId") final String branchId,
            @PathVariable("questionId") final String parentQuestionId,
            @PathVariable("response") final String response,
            @RequestBody final Map<String, String> messageMap
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (messageMap.containsKey("message")) {
            final StatusTreeQuestion addedStatusTreeQuestion =
                    this.statusTreeService.addNewStatusTreeMessage(parentQuestionId, response, messageMap.get("message"));

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(addedStatusTreeQuestion);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).build();
        }


    }
}
