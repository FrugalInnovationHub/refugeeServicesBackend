package com.fih.cr.sjm.tico.mongodb.structures;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@JsonDeserialize
public class TypeValue {
    @NonNull
    private TypeEnum type;
    private String value;
}
