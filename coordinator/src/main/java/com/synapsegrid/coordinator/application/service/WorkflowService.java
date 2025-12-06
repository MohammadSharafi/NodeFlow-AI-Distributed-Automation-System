package com.synapsegrid.coordinator.application.service;

import com.synapsegrid.coordinator.domain.model.Workflow;
import com.synapsegrid.coordinator.domain.repository.WorkflowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkflowService {
    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    public Optional<Workflow> getWorkflowById(String id) {
        return workflowRepository.findById(id);
    }

    public Workflow createWorkflow(String name, String description, List<com.synapsegrid.common.models.WorkflowTask> tasks, String createdBy) {
        Workflow workflow = new Workflow();
        workflow.setId(UUID.randomUUID().toString());
        workflow.setName(name);
        workflow.setDescription(description);
        workflow.setTasks(tasks);
        workflow.setStatus(Workflow.WorkflowStatus.DRAFT);
        workflow.setCreatedBy(createdBy);
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setUpdatedAt(LocalDateTime.now());
        
        return workflowRepository.save(workflow);
    }

    public Workflow updateWorkflow(String id, String name, String description, List<com.synapsegrid.common.models.WorkflowTask> tasks) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        workflow.setName(name);
        workflow.setDescription(description);
        workflow.setTasks(tasks);
        workflow.setUpdatedAt(LocalDateTime.now());
        
        return workflowRepository.save(workflow);
    }

    public Workflow deployWorkflow(String id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        workflow.setStatus(Workflow.WorkflowStatus.ACTIVE);
        workflow.setUpdatedAt(LocalDateTime.now());
        
        return workflowRepository.save(workflow);
    }

    public Workflow pauseWorkflow(String id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        workflow.setStatus(Workflow.WorkflowStatus.PAUSED);
        workflow.setUpdatedAt(LocalDateTime.now());
        
        return workflowRepository.save(workflow);
    }

    public void deleteWorkflow(String id) {
        workflowRepository.deleteById(id);
    }
}

