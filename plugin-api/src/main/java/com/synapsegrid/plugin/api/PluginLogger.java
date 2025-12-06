package com.synapsegrid.plugin.api;

/**
 * Logger interface for plugins
 */
public interface PluginLogger {
    void debug(String message);
    void info(String message);
    void warn(String message);
    void error(String message, Throwable throwable);
}

