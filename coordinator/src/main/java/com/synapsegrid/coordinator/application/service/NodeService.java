package com.synapsegrid.coordinator.application.service;

import com.synapsegrid.coordinator.domain.model.Node;
import com.synapsegrid.coordinator.domain.repository.NodeRepository;
import com.synapsegrid.common.models.NodeCapability;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NodeService {
    private final NodeRepository nodeRepository;

    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public List<Node> getAllNodes() {
        return nodeRepository.findAll();
    }

    public Optional<Node> getNodeById(String id) {
        return nodeRepository.findById(id);
    }

    public Node registerNode(String nodeId, String hostname, NodeCapability capabilities, String grpcAddress, int grpcPort) {
        Node node = nodeRepository.findById(nodeId)
                .orElse(new Node(nodeId, hostname, capabilities));
        
        node.setCapabilities(capabilities);
        node.setGrpcAddress(grpcAddress);
        node.setGrpcPort(grpcPort);
        node.updateHeartbeat();
        node.setStatus(NodeCapability.NodeStatus.ONLINE);
        
        nodeRepository.save(node);
        return node;
    }

    public void updateNodeHeartbeat(String nodeId) {
        nodeRepository.findById(nodeId).ifPresent(node -> {
            node.updateHeartbeat();
            nodeRepository.save(node);
        });
    }

    public List<Node> getHealthyNodes() {
        return nodeRepository.findHealthyNodes();
    }

    public Node selectBestNodeForTask(String pluginId, boolean requiresGPU, int minMemoryMB) {
        List<Node> healthyNodes = getHealthyNodes();
        
        return healthyNodes.stream()
                .filter(node -> {
                    NodeCapability caps = node.getCapabilities();
                    if (requiresGPU && !caps.isHasGPU()) return false;
                    if (caps.getMemoryMB() < minMemoryMB) return false;
                    return caps.getInstalledPlugins().contains(pluginId);
                })
                .max((n1, n2) -> Double.compare(
                    n1.getCapabilities().getCapabilityScore(),
                    n2.getCapabilities().getCapabilityScore()
                ))
                .orElse(null);
    }
}

