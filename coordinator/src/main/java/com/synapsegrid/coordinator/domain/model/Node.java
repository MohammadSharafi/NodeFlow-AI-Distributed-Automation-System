package com.synapsegrid.coordinator.domain.model;

import com.synapsegrid.common.models.NodeCapability;
import java.time.LocalDateTime;

/**
 * Domain model representing a node in the cluster
 */
public class Node {
    private String id;
    private String hostname;
    private NodeCapability.NodeStatus status;
    private NodeCapability capabilities;
    private LocalDateTime lastHeartbeat;
    private String grpcAddress;
    private int grpcPort;

    public Node() {}

    public Node(String id, String hostname, NodeCapability capabilities) {
        this.id = id;
        this.hostname = hostname;
        this.capabilities = capabilities;
        this.status = NodeCapability.NodeStatus.ONLINE;
        this.lastHeartbeat = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public NodeCapability.NodeStatus getStatus() { return status; }
    public void setStatus(NodeCapability.NodeStatus status) { this.status = status; }

    public NodeCapability getCapabilities() { return capabilities; }
    public void setCapabilities(NodeCapability capabilities) { this.capabilities = capabilities; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public String getGrpcAddress() { return grpcAddress; }
    public void setGrpcAddress(String grpcAddress) { this.grpcAddress = grpcAddress; }

    public int getGrpcPort() { return grpcPort; }
    public void setGrpcPort(int grpcPort) { this.grpcPort = grpcPort; }

    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
    }

    public boolean isHealthy() {
        return status == NodeCapability.NodeStatus.ONLINE &&
               lastHeartbeat.isAfter(LocalDateTime.now().minusSeconds(30));
    }
}

