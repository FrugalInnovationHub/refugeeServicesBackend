package com.fih.cr.sjm.tico.mongodb.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class AbstractDocument {

    @Id
    @JsonProperty("_id")
    private String id;
}
