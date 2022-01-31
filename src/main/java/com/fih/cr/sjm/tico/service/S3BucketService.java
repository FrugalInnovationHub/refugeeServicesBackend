package com.fih.cr.sjm.tico.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.utilities.S3URLUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

@Service
public class S3BucketService {
    private final AmazonS3 amazonS3;
    private final S3URLUtil s3URLUtil;
    private final Bucket bucket;
    private final String cloudFrontDomain;

    public S3BucketService(
            final AmazonS3 amazonS3,
            final S3URLUtil s3URLUtil,
            @Value("${amazon.aws.s3.bucket}") final String bucket,
            @Value("${amazon.aws.s3.cloudFront.domain:}") final String cloudFrontDomain
    ) {
        this.amazonS3 = amazonS3;
        this.s3URLUtil = s3URLUtil;
        this.bucket = new Bucket(bucket);
        this.cloudFrontDomain = cloudFrontDomain;
    }

    public URL upload(
            @NonNull final MultipartFile file
    ) throws TicoException, IOException, URISyntaxException {
        if (file.isEmpty()) {
            throw new TicoException("File is Empty");
        }

        if (!this.amazonS3.doesBucketExistV2(this.bucket.getName())) {
            this.amazonS3.createBucket(this.bucket.getName());
        }

        final String fileName = String.format("%s/%s", UUID.randomUUID().toString(), file.getOriginalFilename());

        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());

        final PutObjectRequest putObjectRequest = new PutObjectRequest(
                this.bucket.getName(),
                fileName,
                file.getInputStream(),
                objectMetadata
        );
        putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);

        this.amazonS3.putObject(putObjectRequest);
        final URL s3url = this.amazonS3.getUrl(this.bucket.getName(), fileName);

        if (StringUtils.isEmpty(this.cloudFrontDomain)) {
            return s3URLUtil.getNewCloudFrontURL(s3url, this.cloudFrontDomain).build().toURL();
        }

        return s3url;
    }

    public void delete(
            @NonNull final String fileId
    ) {
        if (this.amazonS3.doesBucketExistV2(this.bucket.getName())) {
            this.amazonS3.deleteObject(this.bucket.getName(), fileId);
        }
    }
}
