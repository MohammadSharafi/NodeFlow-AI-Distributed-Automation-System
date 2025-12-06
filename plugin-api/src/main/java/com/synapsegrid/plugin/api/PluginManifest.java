package com.synapsegrid.plugin.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

/**
 * Plugin manifest describing capabilities and requirements
 */
public class PluginManifest {
    private final String id;
    private final String name;
    private final String version;
    private final String description;
    private final PluginType type;
    private final Set<String> requiredCapabilities;
    private final Set<PluginPermission> permissions;
    private final boolean requiresGPU;
    private final int minMemoryMB;
    private final String author;
    private final String license;

    @JsonCreator
    public PluginManifest(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("version") String version,
            @JsonProperty("description") String description,
            @JsonProperty("type") PluginType type,
            @JsonProperty("requiredCapabilities") Set<String> requiredCapabilities,
            @JsonProperty("permissions") Set<PluginPermission> permissions,
            @JsonProperty("requiresGPU") boolean requiresGPU,
            @JsonProperty("minMemoryMB") int minMemoryMB,
            @JsonProperty("author") String author,
            @JsonProperty("license") String license) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.description = description;
        this.type = type;
        this.requiredCapabilities = requiredCapabilities;
        this.permissions = permissions;
        this.requiresGPU = requiresGPU;
        this.minMemoryMB = minMemoryMB;
        this.author = author;
        this.license = license;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getDescription() { return description; }
    public PluginType getType() { return type; }
    public Set<String> getRequiredCapabilities() { return requiredCapabilities; }
    public Set<PluginPermission> getPermissions() { return permissions; }
    public boolean isRequiresGPU() { return requiresGPU; }
    public int getMinMemoryMB() { return minMemoryMB; }
    public String getAuthor() { return author; }
    public String getLicense() { return license; }

    public enum PluginType {
        TRIGGER,
        ACTION,
        AI_PROCESSOR,
        CONDITION,
        TRANSFORMATION
    }

    public enum PluginPermission {
        FILE_READ,
        FILE_WRITE,
        NETWORK_ACCESS,
        CAMERA_ACCESS,
        MICROPHONE_ACCESS,
        AI_COMPUTE,
        DATABASE_ACCESS
    }
}

