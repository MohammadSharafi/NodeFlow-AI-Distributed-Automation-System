package com.synapsegrid.plugins.whisper;

import com.synapsegrid.plugin.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * AI plugin for speech-to-text transcription using Whisper
 */
public class WhisperTranscriberPlugin implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(WhisperTranscriberPlugin.class);
    
    private String id = "whisper-transcriber";
    private String name = "Whisper Transcriber";
    private String version = "1.0.0";
    private PluginContext context;

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
                "Transcribes audio files to text using Whisper AI model",
                PluginManifest.PluginType.AI_PROCESSOR,
                Set.of("WHISPER"),
                Set.of(PluginManifest.PluginPermission.FILE_READ, PluginManifest.PluginPermission.AI_COMPUTE),
                false, // Can run on CPU
                512,
                "SynapseGrid",
                "MIT"
        );
    }

    @Override
    public TaskResult execute(com.synapsegrid.common.models.WorkflowTask task, Map<String, Object> context) throws PluginException {
        long startTime = System.currentTimeMillis();
        
        try {
            String audioFile = (String) task.getParameters().get("audioFile");
            if (audioFile == null) {
                throw new PluginException("audioFile parameter is required");
            }

            logger.info("Transcribing audio file: {}", audioFile);
            
            // TODO: Integrate with Whisper.cpp via JNI or subprocess
            // For now, return mock result
            String transcript = "Mock transcription of " + audioFile;
            
            Map<String, Object> output = new HashMap<>();
            output.put("transcript", transcript);
            output.put("audioFile", audioFile);
            output.put("duration", 0); // Would be actual duration
            
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
        logger.info("WhisperTranscriberPlugin initialized");
    }

    @Override
    public void shutdown() {
        logger.info("WhisperTranscriberPlugin shut down");
    }
}

