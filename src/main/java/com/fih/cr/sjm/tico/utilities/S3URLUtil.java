package com.fih.cr.sjm.tico.utilities;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Component
public class S3URLUtil {
    private final AmazonS3 amazonS3;

    public S3URLUtil(
            final AmazonS3 amazonS3
    ) {
        this.amazonS3 = amazonS3;
    }
    public URIBuilder findCloudFrontURL(
            final URL oldURL,
            final Bucket bucket,
            final String cloudFrontDomain
    ) throws URISyntaxException {
        final URIBuilder newURI = new URIBuilder(oldURL.toString());
        final List<S3ObjectSummary> objectListing = this.amazonS3.listObjects(
                bucket.getName(),
                newURI.getPathSegments().get(0)
        ).getObjectSummaries();

        final URL url = this.amazonS3.getUrl(bucket.getName(), objectListing.get(0).getKey());

        newURI.setHost(cloudFrontDomain);
        newURI.setPathSegments(new URIBuilder(url.toString()).getPathSegments());

        return newURI;
    }

    public URIBuilder getNewCloudFrontURL(
            final URL oldURL,
            final String cloudFrontDomain
    ) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(oldURL.toString());

        uriBuilder.setHost(cloudFrontDomain);
        return uriBuilder;
    }
}
