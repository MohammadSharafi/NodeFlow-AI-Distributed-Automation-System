package com.synapsegrid.node.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service discovery using mDNS (Bonjour/Zeroconf)
 */
@Service
public class NodeDiscoveryService {
    private static final Logger logger = LoggerFactory.getLogger(NodeDiscoveryService.class);

    private JmDNS jmdns;
    private final String nodeId;
    private final int grpcPort;
    private final ConcurrentMap<String, DiscoveredNode> discoveredNodes = new ConcurrentHashMap<>();

    public NodeDiscoveryService(
            @Value("${node.id}") String nodeId,
            @Value("${grpc.server.port}") int grpcPort) {
        this.nodeId = nodeId;
        this.grpcPort = grpcPort;
    }

    /**
     * Start advertising this node and discovering others
     */
    public void start() {
        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            
            // Advertise this node
            advertiseNode();
            
            // Listen for other nodes
            discoverNodes();
            
            logger.info("Node discovery service started");
        } catch (IOException e) {
            logger.error("Failed to start node discovery", e);
        }
    }

    /**
     * Stop discovery service
     */
    public void stop() {
        if (jmdns != null) {
            try {
                jmdns.unregisterAllServices();
                jmdns.close();
            } catch (IOException e) {
                logger.error("Error stopping discovery service", e);
            }
        }
    }

    /**
     * Advertise this node on the network
     */
    private void advertiseNode() throws IOException {
        ServiceInfo serviceInfo = ServiceInfo.create(
                "_synapsegrid._tcp.local.",
                nodeId,
                grpcPort,
                "SynapseGrid Node Agent"
        );
        
        jmdns.registerService(serviceInfo);
        logger.info("Advertised node {} on port {}", nodeId, grpcPort);
    }

    /**
     * Discover other nodes on the network
     */
    private void discoverNodes() {
        jmdns.addServiceListener("_synapsegrid._tcp.local.", new javax.jmdns.ServiceListener() {
            @Override
            public void serviceAdded(javax.jmdns.ServiceEvent event) {
                logger.info("Service added: {}", event.getName());
            }

            @Override
            public void serviceRemoved(javax.jmdns.ServiceEvent event) {
                logger.info("Service removed: {}", event.getName());
                discoveredNodes.remove(event.getName());
            }

            @Override
            public void serviceResolved(javax.jmdns.ServiceEvent event) {
                ServiceInfo info = event.getInfo();
                DiscoveredNode node = new DiscoveredNode(
                        event.getName(),
                        info.getHostAddress(),
                        info.getPort()
                );
                discoveredNodes.put(event.getName(), node);
                logger.info("Discovered node: {} at {}:{}", event.getName(), node.getAddress(), node.getPort());
            }
        });
    }

    /**
     * Get all discovered nodes
     */
    public List<DiscoveredNode> getDiscoveredNodes() {
        return new ArrayList<>(discoveredNodes.values());
    }

    /**
     * Get a specific discovered node
     */
    public DiscoveredNode getDiscoveredNode(String nodeId) {
        return discoveredNodes.get(nodeId);
    }

    public static class DiscoveredNode {
        private final String nodeId;
        private final String address;
        private final int port;

        public DiscoveredNode(String nodeId, String address, int port) {
            this.nodeId = nodeId;
            this.address = address;
            this.port = port;
        }

        public String getNodeId() { return nodeId; }
        public String getAddress() { return address; }
        public int getPort() { return port; }
    }
}

