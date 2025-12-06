package com.synapsegrid.coordinator.infrastructure.persistence;

import com.synapsegrid.coordinator.domain.model.Node;
import com.synapsegrid.coordinator.domain.repository.NodeRepository;
import com.synapsegrid.common.models.NodeCapability;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of NodeRepository
 * TODO: Replace with JPA implementation
 */
@Component
public class JpaNodeRepository implements NodeRepository {
    private final Map<String, Node> nodes = new ConcurrentHashMap<>();

    @Override
    public List<Node> findAll() {
        return List.copyOf(nodes.values());
    }

    @Override
    public Optional<Node> findById(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    @Override
    public void save(Node node) {
        nodes.put(node.getId(), node);
    }

    @Override
    public void deleteById(String id) {
        nodes.remove(id);
    }

    @Override
    public List<Node> findHealthyNodes() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(30);
        return nodes.values().stream()
                .filter(node -> node.isHealthy() && node.getLastHeartbeat().isAfter(cutoff))
                .toList();
    }

    @Override
    public List<Node> findByStatus(NodeCapability.NodeStatus status) {
        return nodes.values().stream()
                .filter(node -> node.getStatus() == status)
                .toList();
    }
}

