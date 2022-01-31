package com.fih.cr.sjm.tico.controller;

import com.fih.cr.sjm.tico.mongodb.documents.Documentation;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.service.DocumentationService;
import com.fih.cr.sjm.tico.utilities.RolesUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/documentation")
public class DocumentationController {
    private final DocumentationService documentationService;
    private final RolesUtil rolesUtil;

    public DocumentationController(
            final DocumentationService documentationService,
            final RolesUtil rolesUtil
    ) {
        this.documentationService = documentationService;
        this.rolesUtil = rolesUtil;
    }

    @GetMapping
    public ResponseEntity<List<Documentation>> getAllDocumentation(
    ) {
        final List<Documentation> allDocumentation = this.documentationService.getAllDocumentation();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allDocumentation);
    }

    @GetMapping(value = "/page")
    public ResponseEntity<Page<Documentation>> getPagedDocumentation(
            @RequestParam(value = "page", defaultValue = "0") final Integer page,
            @RequestParam(value = "size", defaultValue = "5") final Integer size
    ) {
        final Page<Documentation> allDocumentation = this.documentationService.getPagedDocumentation(page, size);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allDocumentation);
    }

    @GetMapping(value = "/{_id}")
    public ResponseEntity<Documentation> getDocumentationById(
            @PathVariable("_id") final String id
    ) {
        final Optional<Documentation> documentationById = this.documentationService.getDocumentationById(id);

        return documentationById.map(documentation -> ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(documentation)
        ).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Documentation> addNewDocumentation(
            @RequestBody final Documentation documentation
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        documentation.setCreatedAt(new Date().getTime());
        final Documentation addedDocumentation = this.documentationService.addNewDocumentation(documentation);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(addedDocumentation);
    }

    @DeleteMapping(value = "/{_id}")
    public ResponseEntity<Documentation> deleteDocumentationById(
            @PathVariable("_id") final String id
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        this.documentationService.deleteDocumentationById(id);

        return ResponseEntity
                .noContent().build();

    }
}
