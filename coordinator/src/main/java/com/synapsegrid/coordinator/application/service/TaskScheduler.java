package com.synapsegrid.coordinator.application.service;

import com.synapsegrid.coordinator.domain.model.Node;
import com.synapsegrid.common.models.WorkflowTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Schedules tasks to nodes based on capabilities and load
 */
@Service
public class TaskScheduler {
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    private final NodeService nodeService;

    public TaskScheduler(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Schedule a task to the best available node
     */
    public CompletableFuture<Node> scheduleTask(WorkflowTask task) {
        return CompletableFuture.supplyAsync(() -> {
            // Determine requirements from task
            String pluginId = task.getPluginId();
            boolean requiresGPU = false; // TODO: Get from plugin manifest
            int minMemoryMB = 512; // TODO: Get from plugin manifest

            // Select best node
            Node selectedNode = nodeService.selectBestNodeForTask(pluginId, requiresGPU, minMemoryMB);

            if (selectedNode == null) {
                logger.warn("No suitable node found for task {}", task.getTaskId());
                throw new RuntimeException("No suitable node available");
            }

            logger.info("Scheduled task {} to node {}", task.getTaskId(), selectedNode.getId());
            return selectedNode;
        });
    }

    /**
     * Schedule multiple tasks in parallel
     */
    public CompletableFuture<List<Node>> scheduleTasks(List<WorkflowTask> tasks) {
        List<CompletableFuture<Node>> futures = tasks.stream()
                .map(this::scheduleTask)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }
}

