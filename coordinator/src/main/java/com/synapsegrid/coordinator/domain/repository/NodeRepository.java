package com.synapsegrid.coordinator.domain.repository;

import com.synapsegrid.coordinator.domain.model.Node;
import java.util.List;
import java.util.Optional;

public interface NodeRepository {
    List<Node> findAll();
    Optional<Node> findById(String id);
    void save(Node node);
    void deleteById(String id);
    List<Node> findHealthyNodes();
    List<Node> findByStatus(Node.NodeStatus status);
}

