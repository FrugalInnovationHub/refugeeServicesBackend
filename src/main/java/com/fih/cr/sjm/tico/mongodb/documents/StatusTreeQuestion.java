package com.fih.cr.sjm.tico.mongodb.documents;

import com.fih.cr.sjm.tico.mongodb.structures.TypeValue;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;

@Document(collection = "status_tree_questions")
@Builder
@Data
public class StatusTreeQuestion extends AbstractDocument {
    @Field
    @NonNull
    private String question;

    @Field
    @NonNull
    private String branchId;

    @Field
    @Default
    private HashMap<String, ArrayList<TypeValue>> statusTreeResponseOptions;
}
