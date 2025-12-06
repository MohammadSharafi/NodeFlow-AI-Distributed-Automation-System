package com.synapsegrid.coordinator.domain.model;

import com.synapsegrid.common.models.WorkflowTask;
import java.time.LocalDateTime;
import java.util.List;

public class Workflow {
    private String id;
    private String name;
    private String description;
    private List<WorkflowTask> tasks;
    private WorkflowStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Workflow() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<WorkflowTask> getTasks() { return tasks; }
    public void setTasks(List<WorkflowTask> tasks) { this.tasks = tasks; }

    public WorkflowStatus getStatus() { return status; }
    public void setStatus(WorkflowStatus status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum WorkflowStatus {
        DRAFT,
        ACTIVE,
        PAUSED,
        COMPLETED,
        FAILED
    }
}

