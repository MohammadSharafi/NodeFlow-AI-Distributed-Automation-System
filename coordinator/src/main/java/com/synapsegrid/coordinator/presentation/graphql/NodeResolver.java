package com.synapsegrid.coordinator.presentation.graphql;

import com.synapsegrid.coordinator.application.service.NodeService;
import com.synapsegrid.coordinator.domain.model.Node;
import com.synapsegrid.common.models.NodeCapability;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NodeResolver {
    private final NodeService nodeService;

    public NodeResolver(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @QueryMapping
    public List<NodeDTO> nodes() {
        return nodeService.getAllNodes().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public NodeDTO node(@Argument String id) {
        return nodeService.getNodeById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    private NodeDTO toDTO(Node node) {
        NodeDTO dto = new NodeDTO();
        dto.setId(node.getId());
        dto.setHostname(node.getHostname());
        dto.setStatus(node.getStatus().name());
        dto.setCapabilities(toCapabilitiesDTO(node.getCapabilities()));
        dto.setLastHeartbeat(node.getLastHeartbeat().toString());
        return dto;
    }

    private NodeCapabilitiesDTO toCapabilitiesDTO(NodeCapability caps) {
        NodeCapabilitiesDTO dto = new NodeCapabilitiesDTO();
        dto.setCpuCores(caps.getCpuCores());
        dto.setMemoryMB(caps.getMemoryMB());
        dto.setHasGPU(caps.isHasGPU());
        dto.setGpuModel(caps.getGpuModel());
        dto.setInstalledPlugins(caps.getInstalledPlugins());
        dto.setAiCapabilities(caps.getAiCapabilities());
        dto.setDiskSpaceMB(caps.getDiskSpaceMB());
        dto.setCurrentLoad(caps.getCurrentLoad());
        return dto;
    }

    // DTO classes for GraphQL
    public static class NodeDTO {
        private String id;
        private String hostname;
        private String status;
        private NodeCapabilitiesDTO capabilities;
        private String lastHeartbeat;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getHostname() { return hostname; }
        public void setHostname(String hostname) { this.hostname = hostname; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public NodeCapabilitiesDTO getCapabilities() { return capabilities; }
        public void setCapabilities(NodeCapabilitiesDTO capabilities) { this.capabilities = capabilities; }
        public String getLastHeartbeat() { return lastHeartbeat; }
        public void setLastHeartbeat(String lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    }

    public static class NodeCapabilitiesDTO {
        private int cpuCores;
        private long memoryMB;
        private boolean hasGPU;
        private String gpuModel;
        private List<String> installedPlugins;
        private List<String> aiCapabilities;
        private long diskSpaceMB;
        private double currentLoad;

        public int getCpuCores() { return cpuCores; }
        public void setCpuCores(int cpuCores) { this.cpuCores = cpuCores; }
        public long getMemoryMB() { return memoryMB; }
        public void setMemoryMB(long memoryMB) { this.memoryMB = memoryMB; }
        public boolean isHasGPU() { return hasGPU; }
        public void setHasGPU(boolean hasGPU) { this.hasGPU = hasGPU; }
        public String getGpuModel() { return gpuModel; }
        public void setGpuModel(String gpuModel) { this.gpuModel = gpuModel; }
        public List<String> getInstalledPlugins() { return installedPlugins; }
        public void setInstalledPlugins(List<String> installedPlugins) { this.installedPlugins = installedPlugins; }
        public List<String> getAiCapabilities() { return aiCapabilities; }
        public void setAiCapabilities(List<String> aiCapabilities) { this.aiCapabilities = aiCapabilities; }
        public long getDiskSpaceMB() { return diskSpaceMB; }
        public void setDiskSpaceMB(long diskSpaceMB) { this.diskSpaceMB = diskSpaceMB; }
        public double getCurrentLoad() { return currentLoad; }
        public void setCurrentLoad(double currentLoad) { this.currentLoad = currentLoad; }
    }
}

