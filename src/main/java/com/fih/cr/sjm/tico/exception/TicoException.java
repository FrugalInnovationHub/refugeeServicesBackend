package com.fih.cr.sjm.tico.exception;

import java.util.Collections;
import java.util.Map;

public class TicoException extends Exception {
    public TicoException(
            final String message
    ) {
        super(message);
    }

    public Map<String, String> getResponseBody() {
        return Collections.singletonMap("error", getMessage());
    }
}
