package com.synapsegrid.coordinator.infrastructure.persistence;

import com.synapsegrid.coordinator.domain.model.Workflow;
import com.synapsegrid.coordinator.domain.repository.WorkflowRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of WorkflowRepository
 * TODO: Replace with JPA implementation
 */
@Component
public class JpaWorkflowRepository implements WorkflowRepository {
    private final Map<String, Workflow> workflows = new ConcurrentHashMap<>();

    @Override
    public List<Workflow> findAll() {
        return List.copyOf(workflows.values());
    }

    @Override
    public Optional<Workflow> findById(String id) {
        return Optional.ofNullable(workflows.get(id));
    }

    @Override
    public Workflow save(Workflow workflow) {
        workflows.put(workflow.getId(), workflow);
        return workflow;
    }

    @Override
    public void deleteById(String id) {
        workflows.remove(id);
    }

    @Override
    public List<Workflow> findByStatus(Workflow.WorkflowStatus status) {
        return workflows.values().stream()
                .filter(workflow -> workflow.getStatus() == status)
                .toList();
    }
}

