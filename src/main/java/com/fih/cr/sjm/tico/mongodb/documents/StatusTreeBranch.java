package com.fih.cr.sjm.tico.mongodb.documents;

import com.mongodb.lang.Nullable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "status_tree_branch")
@Builder
@Data
public class StatusTreeBranch extends AbstractDocument {
    @Field
    @NonNull
    private String label;

    @Field
    @Nullable
    @Default
    private String description;

    @Field
    @Nullable
    @Default
    private String firstQuestion;
}
