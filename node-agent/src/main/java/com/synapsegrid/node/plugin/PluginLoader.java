package com.synapsegrid.node.plugin;

import com.synapsegrid.plugin.api.Plugin;
import com.synapsegrid.plugin.api.PluginException;
import com.synapsegrid.plugin.api.PluginManifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Loads plugins from JAR files using ServiceLoader pattern
 */
@Component
public class PluginLoader {
    private static final Logger logger = LoggerFactory.getLogger(PluginLoader.class);
    
    private final Map<String, Plugin> loadedPlugins = new HashMap<>();
    private final String pluginDirectory;

    public PluginLoader() {
        this.pluginDirectory = System.getProperty("plugin.directory", "./plugins");
    }

    /**
     * Load all plugins from the plugin directory
     */
    public Map<String, Plugin> loadPlugins() {
        logger.info("Loading plugins from directory: {}", pluginDirectory);
        
        Path pluginDir = Paths.get(pluginDirectory);
        if (!Files.exists(pluginDir)) {
            logger.warn("Plugin directory does not exist: {}", pluginDirectory);
            return loadedPlugins;
        }

        try {
            List<File> jarFiles = Files.list(pluginDir)
                    .filter(path -> path.toString().endsWith(".jar"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for (File jarFile : jarFiles) {
                try {
                    loadPlugin(jarFile);
                } catch (Exception e) {
                    logger.error("Failed to load plugin from {}", jarFile.getName(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Error scanning plugin directory", e);
        }

        logger.info("Loaded {} plugins", loadedPlugins.size());
        return loadedPlugins;
    }

    /**
     * Load a single plugin from a JAR file
     */
    private void loadPlugin(File jarFile) throws Exception {
        logger.info("Loading plugin from: {}", jarFile.getName());

        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarUrl},
                Thread.currentThread().getContextClassLoader()
        );

        // Load manifest
        PluginManifest manifest = loadManifest(jarFile);
        if (manifest == null) {
            logger.warn("No manifest found in {}", jarFile.getName());
            return;
        }

        // Load plugin using ServiceLoader
        ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(
                Plugin.class,
                classLoader
        );

        for (Plugin plugin : serviceLoader) {
            if (plugin.getId().equals(manifest.getId())) {
                loadedPlugins.put(manifest.getId(), plugin);
                logger.info("Successfully loaded plugin: {} v{}", manifest.getName(), manifest.getVersion());
                break;
            }
        }
    }

    /**
     * Load plugin manifest from JAR file
     */
    private PluginManifest loadManifest(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry manifestEntry = jar.getJarEntry("META-INF/plugin-manifest.json");
            if (manifestEntry == null) {
                return null;
            }

            try (var inputStream = jar.getInputStream(manifestEntry)) {
                // TODO: Use Jackson to parse JSON
                // For now, return null - will be implemented with proper JSON parsing
                return null;
            }
        } catch (Exception e) {
            logger.error("Error reading manifest from {}", jarFile.getName(), e);
            return null;
        }
    }

    /**
     * Get a loaded plugin by ID
     */
    public Optional<Plugin> getPlugin(String pluginId) {
        return Optional.ofNullable(loadedPlugins.get(pluginId));
    }

    /**
     * Get all loaded plugins
     */
    public Collection<Plugin> getAllPlugins() {
        return loadedPlugins.values();
    }

    /**
     * Unload a plugin
     */
    public void unloadPlugin(String pluginId) {
        Plugin plugin = loadedPlugins.remove(pluginId);
        if (plugin != null) {
            plugin.shutdown();
            logger.info("Unloaded plugin: {}", pluginId);
        }
    }

    /**
     * Reload all plugins
     */
    public void reloadPlugins() {
        logger.info("Reloading all plugins");
        loadedPlugins.values().forEach(Plugin::shutdown);
        loadedPlugins.clear();
        loadPlugins();
    }
}

