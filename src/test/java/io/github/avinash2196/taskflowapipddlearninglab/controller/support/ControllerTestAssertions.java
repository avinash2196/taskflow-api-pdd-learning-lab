package io.github.avinash2196.taskflowapipddlearninglab.controller.support;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Shared assertions for repeated controller error response checks.
 */
public final class ControllerTestAssertions {

    private ControllerTestAssertions() {
    }

    /**
     * Verifies the common validation error response structure for controller tests.
     *
     * @param resultActions mock MVC result under verification
     * @param fieldName expected failing field name in the response context
     * @throws Exception when the assertion chain fails
     */
    public static void assertValidationError(ResultActions resultActions, String fieldName) throws Exception {
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.context.field").value(fieldName));
    }

    /**
     * Verifies the common not-found error response structure for controller tests.
     *
     * @param resultActions mock MVC result under verification
     * @param errorCode expected machine-readable error code
     * @param fieldName expected context field name in the response
     * @throws Exception when the assertion chain fails
     */
    public static void assertNotFoundError(
            ResultActions resultActions,
            String errorCode,
            String fieldName) throws Exception {
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.context.field").value(fieldName));
    }
}
