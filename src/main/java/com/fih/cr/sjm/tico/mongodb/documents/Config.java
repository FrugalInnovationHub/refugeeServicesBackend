package com.fih.cr.sjm.tico.mongodb.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "config")
@Builder
@Data
public class Config {

    @Id
    private String key;

    @Field
    private Object value;

}
