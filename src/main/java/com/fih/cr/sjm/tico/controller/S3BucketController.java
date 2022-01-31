package com.fih.cr.sjm.tico.controller;

import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.responsebody.S3BucketUploadResponse;
import com.fih.cr.sjm.tico.service.S3BucketService;
import com.fih.cr.sjm.tico.utilities.RolesUtil;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@Validated
@RestController
@RequestMapping("/s3")
public class S3BucketController {
    private final S3BucketService s3BucketService;
    private final RolesUtil rolesUtil;

    public S3BucketController(
            final S3BucketService s3BucketService,
            final RolesUtil rolesUtil
    ) {
        this.s3BucketService = s3BucketService;
        this.rolesUtil = rolesUtil;
    }

    @SneakyThrows
    @PostMapping("/upload")
    public ResponseEntity<S3BucketUploadResponse> upload(
            @RequestParam("file") final MultipartFile file
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final URL url = this.s3BucketService.upload(file);
        final String originalFilename = file.getOriginalFilename();

        final String fileExtension;
        final String filename;
        if (originalFilename != null) {
            fileExtension = Files.getFileExtension(originalFilename);
            filename = Files.getNameWithoutExtension(originalFilename);
        } else {
            fileExtension = "";
            filename = "unknown";
        }

        final S3BucketUploadResponse s3BucketUploadResponse = S3BucketUploadResponse.builder()
                .filename(filename)
                .url(url)
                .contentType(file.getContentType())
                .fileExtension(fileExtension)
                .build();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(s3BucketUploadResponse);
    }
}
