package com.fih.cr.sjm.tico.mongodb.documents;

import com.fih.cr.sjm.tico.mongodb.structures.Options;
import com.fih.cr.sjm.tico.mongodb.structures.TypeValue;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


@Document(collection = "questions")
@Builder
@Data
public class Question extends AbstractDocument {

    @Field
    @Singular("promptItem")
    private List<TypeValue> prompt;

    @Field
    private Options options;

    @Field
    @NonNull
    private String answer;

    @Field
    @Singular("explanationItem")
    private List<TypeValue> explanation;

}
