package com.synapsegrid.coordinator.presentation.graphql;

import com.synapsegrid.coordinator.application.service.WorkflowService;
import com.synapsegrid.coordinator.domain.model.Workflow;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WorkflowResolver {
    private final WorkflowService workflowService;

    public WorkflowResolver(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @QueryMapping
    public List<WorkflowDTO> workflows() {
        return workflowService.getAllWorkflows().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public WorkflowDTO workflow(@Argument String id) {
        return workflowService.getWorkflowById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @MutationMapping
    public WorkflowDTO createWorkflow(@Argument WorkflowInput input) {
        Workflow workflow = workflowService.createWorkflow(
                input.getName(),
                input.getDescription(),
                input.getTasks(),
                "system" // TODO: Get from authentication context
        );
        return toDTO(workflow);
    }

    @MutationMapping
    public WorkflowDTO updateWorkflow(@Argument String id, @Argument WorkflowInput input) {
        Workflow workflow = workflowService.updateWorkflow(
                id,
                input.getName(),
                input.getDescription(),
                input.getTasks()
        );
        return toDTO(workflow);
    }

    @MutationMapping
    public WorkflowDTO deployWorkflow(@Argument String id) {
        Workflow workflow = workflowService.deployWorkflow(id);
        return toDTO(workflow);
    }

    @MutationMapping
    public WorkflowDTO pauseWorkflow(@Argument String id) {
        Workflow workflow = workflowService.pauseWorkflow(id);
        return toDTO(workflow);
    }

    @MutationMapping
    public Boolean deleteWorkflow(@Argument String id) {
        workflowService.deleteWorkflow(id);
        return true;
    }

    private WorkflowDTO toDTO(Workflow workflow) {
        WorkflowDTO dto = new WorkflowDTO();
        dto.setId(workflow.getId());
        dto.setName(workflow.getName());
        dto.setDescription(workflow.getDescription());
        dto.setStatus(workflow.getStatus().name());
        dto.setTasks(workflow.getTasks());
        dto.setCreatedAt(workflow.getCreatedAt().toString());
        dto.setUpdatedAt(workflow.getUpdatedAt().toString());
        return dto;
    }

    public static class WorkflowDTO {
        private String id;
        private String name;
        private String description;
        private String status;
        private List<com.synapsegrid.common.models.WorkflowTask> tasks;
        private String createdAt;
        private String updatedAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<com.synapsegrid.common.models.WorkflowTask> getTasks() { return tasks; }
        public void setTasks(List<com.synapsegrid.common.models.WorkflowTask> tasks) { this.tasks = tasks; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class WorkflowInput {
        private String name;
        private String description;
        private List<WorkflowTaskInput> tasks;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<WorkflowTaskInput> getTasks() { return tasks; }
        public void setTasks(List<WorkflowTaskInput> tasks) { this.tasks = tasks; }
    }

    public static class WorkflowTaskInput {
        private String pluginId;
        private String type;
        private Map<String, Object> parameters;
        private List<String> dependencies;

        public String getPluginId() { return pluginId; }
        public void setPluginId(String pluginId) { this.pluginId = pluginId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    }
}

