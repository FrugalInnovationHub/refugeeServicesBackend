package com.fih.cr.sjm.tico.requestbody;

import com.fih.cr.sjm.tico.mongodb.structures.TypeValue;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

@Builder
@Data
public class AddStatusTreeQuestionRequest {
    @NonNull
    private String question;

    @Default
    private HashMap<String, ArrayList<TypeValue>> statusTreeResponseOptions;
}
