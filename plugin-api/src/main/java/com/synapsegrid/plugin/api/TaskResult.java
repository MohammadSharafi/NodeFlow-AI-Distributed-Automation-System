package com.synapsegrid.plugin.api;

import java.util.Map;

/**
 * Result of a plugin task execution
 */
public class TaskResult {
    private final boolean success;
    private final Map<String, Object> output;
    private final String errorMessage;
    private final long executionTimeMs;

    private TaskResult(boolean success, Map<String, Object> output, String errorMessage, long executionTimeMs) {
        this.success = success;
        this.output = output;
        this.errorMessage = errorMessage;
        this.executionTimeMs = executionTimeMs;
    }

    public static TaskResult success(Map<String, Object> output, long executionTimeMs) {
        return new TaskResult(true, output, null, executionTimeMs);
    }

    public static TaskResult failure(String errorMessage, long executionTimeMs) {
        return new TaskResult(false, null, errorMessage, executionTimeMs);
    }

    public boolean isSuccess() { return success; }
    public Map<String, Object> getOutput() { return output; }
    public String getErrorMessage() { return errorMessage; }
    public long getExecutionTimeMs() { return executionTimeMs; }
}

