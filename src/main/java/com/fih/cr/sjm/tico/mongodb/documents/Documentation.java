package com.fih.cr.sjm.tico.mongodb.documents;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.net.URL;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Document(collection = "documentation")
@Builder
@Data
public class Documentation extends AbstractDocument {

    @Field
    private String filename;

    @Field
    private URL url;

    @Field
    private String contentType;

    @Field
    private String fileExtension;

    @Field
    private String category;

    @Field
    private long createdAt;

    @Field
    private List<String> tags;
}
