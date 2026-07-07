package io.github.avinash2196.taskflowapipddlearninglab.exception;

import java.util.Map;

/**
 * Shared API error payload used by controller exception handling.
 */
public class ApiErrorResponse {

    private String errorCode;
    private String message;
    private Map<String, String> context;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String errorCode, String message, Map<String, String> context) {
        this.errorCode = errorCode;
        this.message = message;
        this.context = context;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
