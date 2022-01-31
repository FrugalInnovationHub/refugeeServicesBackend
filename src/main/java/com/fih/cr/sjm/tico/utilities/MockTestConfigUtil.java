package com.fih.cr.sjm.tico.utilities;

import com.fih.cr.sjm.tico.mongodb.documents.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MockTestConfigUtil {
    public static final String SAMPLE_SIZE = "mockTest.size.sample";
    public static final String MAX_SIZE = "mockTest.size.max";
    public static final String PASS_PERCENTAGE = "mockTest.pass.percentage";
    public static final String PASS_MESSAGE = "mockTest.pass.message";
    public static final String FAIL_MESSAGE = "mockTest.fail.message";

    public static final List<String> CONFIG_LIST = Collections.unmodifiableList(
            Arrays.asList(
                    MockTestConfigUtil.SAMPLE_SIZE,

                    MockTestConfigUtil.PASS_PERCENTAGE,
                    MockTestConfigUtil.PASS_MESSAGE,

                    MockTestConfigUtil.FAIL_MESSAGE
            )
    );

    public static List<Config> sanitizeUpdateRequest(
            final List<Config> configList
    ) {
        return configList.parallelStream()
                .peek(config -> {

                    switch (config.getKey()) {
                        case SAMPLE_SIZE:
                        case PASS_PERCENTAGE:
                            if (config.getValue() != null) {
                                config.setValue(Integer.parseInt(String.valueOf(config.getValue())));
                            }
                            break;
                        case PASS_MESSAGE:
                        case FAIL_MESSAGE:
                            config.setValue(String.valueOf(config.getValue()));
                            break;
                        default:
                            break;
                    }

                })
                .collect(Collectors.toList());
    }
}
