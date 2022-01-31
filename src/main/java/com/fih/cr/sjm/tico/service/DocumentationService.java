package com.fih.cr.sjm.tico.service;

import com.fih.cr.sjm.tico.mongodb.documents.Documentation;
import com.fih.cr.sjm.tico.mongodb.repository.DocumentationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentationService {
    private final DocumentationRepository documentationRepository;
    private final S3BucketService s3BucketService;

    public DocumentationService(
            final DocumentationRepository documentationRepository,
            final S3BucketService s3BucketService
    ) {
        this.documentationRepository = documentationRepository;
        this.s3BucketService = s3BucketService;
    }

    public List<Documentation> getAllDocumentation() {
        return this.documentationRepository.findAll();
    }

    public Page<Documentation> getPagedDocumentation(
            final Integer page,
            final Integer size
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        return this.documentationRepository.findAll(pageable);
    }


    public Documentation addNewDocumentation(
            final Documentation documentation
    ) {
        return this.documentationRepository.insert(documentation);
    }

    public Optional<Documentation> getDocumentationById(
            final String documentationId
    ) {
        return this.documentationRepository.findById(documentationId);
    }

    public void deleteDocumentationById(
            final String documentationId
    ) {
        this.documentationRepository.findById(documentationId).ifPresent(documentation -> {
            final String fileId = documentation.getUrl().getPath();

            this.s3BucketService.delete(fileId);
            this.documentationRepository.deleteById(documentationId);
        });
    }

    public void updateDocumentationURL(
            final String documentationId,
            final URL url
    ) {
        this.documentationRepository.findById(documentationId).ifPresent(documentation -> {
            documentation.setUrl(url);
            this.documentationRepository.save(documentation);
        });
    }
}
