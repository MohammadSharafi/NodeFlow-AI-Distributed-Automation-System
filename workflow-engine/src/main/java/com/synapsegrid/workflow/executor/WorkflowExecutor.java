package com.synapsegrid.workflow.executor;

import com.synapsegrid.common.models.WorkflowTask;
import com.synapsegrid.plugin.api.Plugin;
import com.synapsegrid.plugin.api.TaskResult;
import com.synapsegrid.workflow.graph.WorkflowGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Executes workflows represented as DAGs
 */
public class WorkflowExecutor {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowExecutor.class);

    private final PluginProvider pluginProvider;
    private final Map<String, Map<String, Object>> executionContext;

    public WorkflowExecutor(PluginProvider pluginProvider) {
        this.pluginProvider = pluginProvider;
        this.executionContext = new ConcurrentHashMap<>();
    }

    /**
     * Execute a workflow graph
     */
    public CompletableFuture<ExecutionResult> execute(WorkflowGraph graph) {
        CompletableFuture<ExecutionResult> future = new CompletableFuture<>();
        
        Set<String> completedTasks = ConcurrentHashMap.newKeySet();
        Set<String> failedTasks = ConcurrentHashMap.newKeySet();
        Map<String, TaskResult> taskResults = new ConcurrentHashMap<>();

        executeWorkflow(graph, completedTasks, failedTasks, taskResults, future);

        return future;
    }

    private void executeWorkflow(
            WorkflowGraph graph,
            Set<String> completedTasks,
            Set<String> failedTasks,
            Map<String, TaskResult> taskResults,
            CompletableFuture<ExecutionResult> future) {

        List<String> readyTasks = graph.getReadyTasks(completedTasks);

        if (readyTasks.isEmpty()) {
            if (completedTasks.size() == graph.getAllTasks().size()) {
                future.complete(new ExecutionResult(true, taskResults, null));
            } else if (!failedTasks.isEmpty()) {
                future.complete(new ExecutionResult(false, taskResults, "Some tasks failed"));
            }
            return;
        }

        // Execute ready tasks in parallel
        List<CompletableFuture<TaskExecution>> taskFutures = readyTasks.stream()
                .map(taskId -> executeTask(graph, taskId, taskResults))
                .collect(Collectors.toList());

        CompletableFuture.allOf(taskFutures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    for (CompletableFuture<TaskExecution> taskFuture : taskFutures) {
                        try {
                            TaskExecution execution = taskFuture.get();
                            if (execution.success) {
                                completedTasks.add(execution.taskId);
                                taskResults.put(execution.taskId, execution.result);
                            } else {
                                failedTasks.add(execution.taskId);
                            }
                        } catch (Exception e) {
                            logger.error("Error executing task", e);
                        }
                    }

                    // Continue with next level of tasks
                    executeWorkflow(graph, completedTasks, failedTasks, taskResults, future);
                });
    }

    private CompletableFuture<TaskExecution> executeTask(
            WorkflowGraph graph,
            String taskId,
            Map<String, TaskResult> previousResults) {

        return CompletableFuture.supplyAsync(() -> {
            WorkflowTask task = graph.getTask(taskId);
            long startTime = System.currentTimeMillis();

            try {
                Optional<Plugin> pluginOpt = pluginProvider.getPlugin(task.getPluginId());
                if (pluginOpt.isEmpty()) {
                    return new TaskExecution(taskId, false, null, "Plugin not found: " + task.getPluginId());
                }

                Plugin plugin = pluginOpt.get();
                
                // Build context from previous task results
                Map<String, Object> context = buildContext(task, previousResults);
                
                // Execute plugin
                TaskResult result = plugin.execute(task, context);
                
                long executionTime = System.currentTimeMillis() - startTime;
                logger.info("Task {} executed in {}ms", taskId, executionTime);

                return new TaskExecution(taskId, result.isSuccess(), result, null);
            } catch (Exception e) {
                logger.error("Error executing task {}", taskId, e);
                long executionTime = System.currentTimeMillis() - startTime;
                return new TaskExecution(taskId, false, null, e.getMessage());
            }
        });
    }

    private Map<String, Object> buildContext(WorkflowTask task, Map<String, TaskResult> previousResults) {
        Map<String, Object> context = new HashMap<>();
        
        // Add outputs from dependency tasks
        if (task.getDependencies() != null) {
            for (String depId : task.getDependencies()) {
                TaskResult depResult = previousResults.get(depId);
                if (depResult != null && depResult.getOutput() != null) {
                    context.put(depId, depResult.getOutput());
                }
            }
        }
        
        return context;
    }

    /**
     * Provider interface for getting plugins
     */
    public interface PluginProvider {
        Optional<Plugin> getPlugin(String pluginId);
    }

    private static class TaskExecution {
        final String taskId;
        final boolean success;
        final TaskResult result;
        final String error;

        TaskExecution(String taskId, boolean success, TaskResult result, String error) {
            this.taskId = taskId;
            this.success = success;
            this.result = result;
            this.error = error;
        }
    }

    public static class ExecutionResult {
        private final boolean success;
        private final Map<String, TaskResult> taskResults;
        private final String errorMessage;

        public ExecutionResult(boolean success, Map<String, TaskResult> taskResults, String errorMessage) {
            this.success = success;
            this.taskResults = taskResults;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public Map<String, TaskResult> getTaskResults() { return taskResults; }
        public String getErrorMessage() { return errorMessage; }
    }
}

