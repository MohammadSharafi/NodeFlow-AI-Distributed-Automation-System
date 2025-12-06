# SynapseGrid - Distributed AI Automation Platform

<div align="center">

![SynapseGrid](https://img.shields.io/badge/SynapseGrid-Distributed%20Automation-blue?style=for-the-badge)
![Privacy](https://img.shields.io/badge/Privacy-First-green?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge)

**A distributed, privacy-first automation platform that runs entirely on user-controlled nodes**

[Architecture](#-architecture) • [Quick Start](#-quick-start) • [Documentation](#-documentation)

</div>

---

## 📖 Description

SynapseGrid is a distributed automation platform that enables users to create complex automation workflows similar to Zapier or IFTTT, but with a crucial difference: **all actions, triggers, AI tasks, and integrations run locally on user-controlled nodes**, not in the cloud.

### Key Differentiators

🔒 **Privacy-First**: All processing happens on your infrastructure - no cloud dependencies

🌐 **Distributed**: Multiple devices form a cluster, sharing workload and AI processing

🧩 **Extensible**: Modular plugin system allows easy addition of new triggers, actions, and AI processors

🤖 **AI-Powered**: Local AI models (Whisper, Llama, ONNX) for advanced automation

⚡ **Fault-Tolerant**: Automatic task reassignment and recovery on node failures

🔐 **Secure**: Mutual TLS, plugin sandboxing, and capability-based access control

## 🏗️ Architecture

### System Components

```
┌─────────────────────────────────────────────────────────┐
│                  Coordinator Service                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐             │
│  │GraphQL   │  │Scheduler │  │Node      │             │
│  │API       │  │          │  │Manager   │             │
│  └──────────┘  └──────────┘  └──────────┘             │
└──────────────────────┬──────────────────────────────────┘
                       │ gRPC / Kafka
┌──────────────────────▼──────────────────────────────────┐
│              Node Agent (Multiple Instances)             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐             │
│  │Plugin    │  │Workflow  │  │AI        │             │
│  │Loader    │  │Executor  │  │Engine    │             │
│  └──────────┘  └──────────┘  └──────────┘             │
└─────────────────────────────────────────────────────────┘
```

### Technology Stack

**Backend**
- Java 17+
- Spring Boot 3.2+ (Coordinator)
- Micronaut 4.0+ (Node Agent)
- GraphQL (Spring GraphQL)
- gRPC for inter-node communication
- Apache Kafka for event streaming
- PostgreSQL for state management
- Redis for coordination

**AI Integration**
- JNI bindings for native models
- Subprocess execution for AI tools
- gRPC adapters for remote AI services
- Support for Whisper.cpp, Llama.cpp, ONNX models

**Infrastructure**
- Docker Compose
- Kafka cluster
- PostgreSQL
- Redis
- mDNS for service discovery

## 🎯 Core Features

### ✅ Distributed Architecture
- Multi-node network with automatic discovery
- Capability-based task distribution
- Load balancing and health monitoring
- Horizontal scaling

### ✅ Workflow Engine
- Directed graph (DAG) representation
- Parallel task execution
- Retry and fault tolerance
- Branching and conditional logic
- Asynchronous event handling

### ✅ Plugin System
- Runtime plugin loader (ServiceLoader)
- Hot-swappable modules
- Manifest-based capability declaration
- Permission sandboxing
- Plugin registry

### ✅ AI Integration
- Local AI model execution
- GPU-aware task scheduling
- Support for multiple AI frameworks
- Custom AI processor plugins

### ✅ Security
- Mutual TLS authentication
- Public-key signed plugin manifests
- Capability-based access control
- Audit trails
- Encrypted inter-node communication

### ✅ Control Plane
- GraphQL API
- Visual workflow editor
- Real-time monitoring
- Node health dashboards
- Plugin management

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15+
- Redis 7+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/MohammadSharafi/NodeFlow-AI-Distributed-Automation-System.git
cd NodeFlow-AI-Distributed-Automation-System
```

2. **Start infrastructure**
```bash
cd docker
docker-compose up -d
```

3. **Build the project**
```bash
mvn clean install
```

4. **Start Coordinator**
```bash
cd coordinator
mvn spring-boot:run
```

5. **Start Node Agent**
```bash
cd node-agent
mvn spring-boot:run
```

## 📚 Documentation

- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - Detailed architecture documentation
- **[PLUGIN_GUIDE.md](docs/PLUGIN_GUIDE.md)** - Plugin development guide
- **[WORKFLOW_GUIDE.md](docs/WORKFLOW_GUIDE.md)** - Workflow creation guide

## 🛠️ Development

### Project Structure

```
synapsegrid/
├── coordinator/          # Central orchestration service
├── node-agent/           # Node agent service
├── workflow-engine/      # Workflow execution engine
├── plugin-api/           # Plugin SDK
├── common/               # Shared libraries
├── plugins/              # Example plugins
├── frontend/             # Dashboard UI
└── docker/               # Infrastructure
```

### Building

```bash
# Build all modules
mvn clean install

# Build specific module
cd coordinator && mvn clean install
```

### Running Tests

```bash
mvn test
```

## 📄 License

Private project - All rights reserved

---

<div align="center">

**Built for developers who value privacy and control**

[⭐ Star this repo](https://github.com/MohammadSharafi/NodeFlow-AI-Distributed-Automation-System) if you find it useful!

</div>

