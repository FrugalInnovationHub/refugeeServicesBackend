package com.fih.cr.sjm.tico.responsebody;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
@JsonDeserialize
public class S3BucketUploadResponse {
    private URL url;
    private String contentType;
    private String fileExtension;
    private String filename;
}
