package com.synapsegrid.plugin.api;

import com.synapsegrid.common.models.WorkflowTask;
import java.util.Map;

/**
 * Base interface for all SynapseGrid plugins
 */
public interface Plugin {
    /**
     * Get the plugin's unique identifier
     */
    String getId();

    /**
     * Get the plugin's name
     */
    String getName();

    /**
     * Get the plugin's version
     */
    String getVersion();

    /**
     * Get the plugin's manifest
     */
    PluginManifest getManifest();

    /**
     * Execute a task
     */
    TaskResult execute(WorkflowTask task, Map<String, Object> context) throws PluginException;

    /**
     * Initialize the plugin
     */
    void initialize(PluginContext context) throws PluginException;

    /**
     * Shutdown the plugin
     */
    void shutdown();
}

