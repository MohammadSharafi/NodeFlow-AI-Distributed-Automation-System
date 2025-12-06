package com.synapsegrid.plugin.api;

import java.util.Map;

/**
 * Context provided to plugins during initialization
 */
public interface PluginContext {
    /**
     * Get configuration for this plugin
     */
    Map<String, Object> getConfig();

    /**
     * Get a logger for this plugin
     */
    PluginLogger getLogger();

    /**
     * Get the node ID where this plugin is running
     */
    String getNodeId();
}

