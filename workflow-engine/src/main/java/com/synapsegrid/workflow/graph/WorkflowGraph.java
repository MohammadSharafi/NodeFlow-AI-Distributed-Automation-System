package com.synapsegrid.workflow.graph;

import com.synapsegrid.common.models.WorkflowTask;
import java.util.*;

/**
 * Represents a workflow as a directed acyclic graph (DAG)
 */
public class WorkflowGraph {
    private final Map<String, WorkflowTask> tasks;
    private final Map<String, List<String>> adjacencyList;
    private final Map<String, List<String>> reverseAdjacencyList;
    private final List<String> rootTasks;

    public WorkflowGraph(List<WorkflowTask> tasks) {
        this.tasks = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.reverseAdjacencyList = new HashMap<>();
        this.rootTasks = new ArrayList<>();

        // Build graph structure
        for (WorkflowTask task : tasks) {
            this.tasks.put(task.getTaskId(), task);
            this.adjacencyList.put(task.getTaskId(), new ArrayList<>());
            this.reverseAdjacencyList.put(task.getTaskId(), new ArrayList<>());
        }

        // Build edges
        for (WorkflowTask task : tasks) {
            if (task.getDependencies() == null || task.getDependencies().length == 0) {
                rootTasks.add(task.getTaskId());
            } else {
                for (String depId : task.getDependencies()) {
                    adjacencyList.get(depId).add(task.getTaskId());
                    reverseAdjacencyList.get(task.getTaskId()).add(depId);
                }
            }
        }

        // Validate DAG (check for cycles)
        if (hasCycle()) {
            throw new IllegalArgumentException("Workflow contains cycles");
        }
    }

    /**
     * Get all tasks that can be executed (all dependencies completed)
     */
    public List<String> getReadyTasks(Set<String> completedTasks) {
        List<String> ready = new ArrayList<>();
        
        for (String taskId : tasks.keySet()) {
            if (completedTasks.contains(taskId)) {
                continue;
            }

            List<String> dependencies = reverseAdjacencyList.get(taskId);
            if (dependencies.isEmpty() || completedTasks.containsAll(dependencies)) {
                ready.add(taskId);
            }
        }
        
        return ready;
    }

    /**
     * Get root tasks (tasks with no dependencies)
     */
    public List<String> getRootTasks() {
        return new ArrayList<>(rootTasks);
    }

    /**
     * Get task by ID
     */
    public WorkflowTask getTask(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * Get all tasks
     */
    public Collection<WorkflowTask> getAllTasks() {
        return tasks.values();
    }

    /**
     * Get tasks that depend on this task
     */
    public List<String> getDependents(String taskId) {
        return new ArrayList<>(adjacencyList.getOrDefault(taskId, Collections.emptyList()));
    }

    /**
     * Check if graph has cycles using DFS
     */
    private boolean hasCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String taskId : tasks.keySet()) {
            if (hasCycleDFS(taskId, visited, recursionStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCycleDFS(String taskId, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(taskId)) {
            return true; // Cycle detected
        }
        if (visited.contains(taskId)) {
            return false;
        }

        visited.add(taskId);
        recursionStack.add(taskId);

        for (String dependent : adjacencyList.get(taskId)) {
            if (hasCycleDFS(dependent, visited, recursionStack)) {
                return true;
            }
        }

        recursionStack.remove(taskId);
        return false;
    }

    /**
     * Topological sort of tasks
     */
    public List<String> topologicalSort() {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> tempMark = new HashSet<>();

        for (String taskId : tasks.keySet()) {
            if (!visited.contains(taskId)) {
                topologicalSortDFS(taskId, visited, tempMark, result);
            }
        }

        Collections.reverse(result);
        return result;
    }

    private void topologicalSortDFS(String taskId, Set<String> visited, Set<String> tempMark, List<String> result) {
        if (tempMark.contains(taskId)) {
            throw new IllegalArgumentException("Cycle detected in workflow");
        }
        if (visited.contains(taskId)) {
            return;
        }

        tempMark.add(taskId);

        for (String dependent : adjacencyList.get(taskId)) {
            topologicalSortDFS(dependent, visited, tempMark, result);
        }

        tempMark.remove(taskId);
        visited.add(taskId);
        result.add(taskId);
    }
}

