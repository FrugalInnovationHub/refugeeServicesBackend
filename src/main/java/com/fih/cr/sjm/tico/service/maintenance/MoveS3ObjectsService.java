package com.fih.cr.sjm.tico.service.maintenance;

import com.amazonaws.services.s3.model.Bucket;
import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.mongodb.structures.TypeEnum;
import com.fih.cr.sjm.tico.service.DocumentationService;
import com.fih.cr.sjm.tico.service.QuestionService;
import com.fih.cr.sjm.tico.utilities.S3URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Service
public class MoveS3ObjectsService {
    private final DocumentationService documentationService;
    private final QuestionService questionService;

    private final S3URLUtil s3URLUtil;
    private final Bucket bucket;

    public MoveS3ObjectsService(
            final DocumentationService documentationService,
            final QuestionService questionService,

            final S3URLUtil s3URLUtil,
            @Value("${amazon.aws.s3.bucket}") final String bucket
    ) {
        this.documentationService = documentationService;
        this.questionService = questionService;

        this.s3URLUtil = s3URLUtil;
        this.bucket = new Bucket(bucket);
    }

    public void moveS3Objects(
            final String cloudFrontDomain
    ) {
        // Change all documentation urls
        this.documentationService.getAllDocumentation().parallelStream().forEach(documentation -> {
            final URL s3url = documentation.getUrl();

            try {

                this.documentationService.updateDocumentationURL(
                        documentation.getId(),
                        s3URLUtil.findCloudFrontURL(s3url, this.bucket, cloudFrontDomain).build().toURL()
                );
            } catch (MalformedURLException | URISyntaxException e) {
                log.error("Error changing documentation: {}", documentation.getId());
                e.printStackTrace();
            }
        });

        this.questionService.findAllQuestions().parallelStream().forEach(question -> {
            // Change all prompt images URL
            question.getPrompt().parallelStream().forEach(typeValue -> {
                if (TypeEnum.IMG.equals(typeValue.getType()) && !StringUtils.isEmpty(typeValue.getValue())) {
                    try {
                        typeValue.setValue(
                                s3URLUtil.findCloudFrontURL(
                                        new URL(typeValue.getValue()),
                                        this.bucket,
                                        cloudFrontDomain
                                ).build().toASCIIString());
                    } catch (MalformedURLException | URISyntaxException e) {
                        log.error("Error changing question prompt: {} {}", question.getId(), typeValue.getValue());
                        e.printStackTrace();
                    }
                }
            });

            // Change all explanation images URL
            question.getExplanation().parallelStream().forEach(typeValue -> {
                if (TypeEnum.IMG.equals(typeValue.getType()) && !StringUtils.isEmpty(typeValue.getValue())) {
                    try {
                        typeValue.setValue(
                                s3URLUtil.findCloudFrontURL(
                                        new URL(typeValue.getValue()),
                                        this.bucket,
                                        cloudFrontDomain
                                ).build().toASCIIString());
                    } catch (MalformedURLException | URISyntaxException e) {
                        log.error("Error changing question explanation: {} {}", question.getId(), typeValue.getValue());
                        e.printStackTrace();
                    }
                }
            });

            try {
                this.questionService.updateQuestion(question.getId(), question);
            } catch (TicoException e) {
                log.error("Error changing question: {}", question.getId());
                e.printStackTrace();
            }
        });
    }
}
