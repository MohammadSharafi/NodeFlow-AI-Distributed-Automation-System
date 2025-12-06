package com.synapsegrid.plugins.filewatcher;

import com.synapsegrid.plugin.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Example plugin that watches for file changes
 */
public class FileWatcherPlugin implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(FileWatcherPlugin.class);
    
    private String id = "file-watcher";
    private String name = "File Watcher";
    private String version = "1.0.0";
    private PluginContext context;
    private WatchService watchService;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public PluginManifest getManifest() {
        return new PluginManifest(
                id,
                name,
                version,
                "Watches for file changes in a directory",
                PluginManifest.PluginType.TRIGGER,
                Set.of(),
                Set.of(PluginManifest.PluginPermission.FILE_READ),
                false,
                128,
                "SynapseGrid",
                "MIT"
        );
    }

    @Override
    public TaskResult execute(com.synapsegrid.common.models.WorkflowTask task, Map<String, Object> context) throws PluginException {
        long startTime = System.currentTimeMillis();
        
        try {
            String directory = (String) task.getParameters().get("directory");
            if (directory == null) {
                throw new PluginException("Directory parameter is required");
            }

            logger.info("Watching directory: {}", directory);
            
            // Start watching (simplified - in real implementation would use WatchService)
            Map<String, Object> output = new HashMap<>();
            output.put("directory", directory);
            output.put("status", "watching");
            output.put("message", "File watcher started for " + directory);
            
            long executionTime = System.currentTimeMillis() - startTime;
            return TaskResult.success(output, executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return TaskResult.failure(e.getMessage(), executionTime);
        }
    }

    @Override
    public void initialize(PluginContext context) throws PluginException {
        this.context = context;
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            logger.info("FileWatcherPlugin initialized");
        } catch (Exception e) {
            throw new PluginException("Failed to initialize FileWatcherPlugin", e);
        }
    }

    @Override
    public void shutdown() {
        if (watchService != null) {
            try {
                watchService.close();
            } catch (Exception e) {
                logger.error("Error closing watch service", e);
            }
        }
        logger.info("FileWatcherPlugin shut down");
    }
}

