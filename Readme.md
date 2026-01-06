# OSB Pipeline Orchestration

A unified Spring Boot orchestration framework for managing Oracle Service Bus (OSB) pipeline flows.

## Overview

This application provides a single, configurable orchestration layer for processing messages through different OSB pipeline flows (B2B, Siebel, Portal, Flow, NEO, and Sync). It replaces static, flow-specific helpers with a dynamic, configuration-driven approach.

## Key Features

- **Unified Orchestration**: Single `McpGenericHelper` service handles all flow types
- **Configuration-Driven**: Flow behavior defined in YAML configuration
- **Dynamic Routing**: Automatically selects flow based on queue name
- **Header Validation**: Validates required headers per flow specification
- **Schema Validation**: Optional XML schema validation per flow
- **Transformation Support**: XQuery transformation pipeline
- **Adapter Integration**: Flexible routing to backend adapter endpoints

## Architecture

### Core Components

1. **FlowSpec** - POJO representing flow metadata:
   - Flow name and queue name mapping
   - Required headers list
   - XQuery transformation path
   - Adapter endpoint URL
   - Schema validation configuration

2. **FlowConfiguration** - Loads flow specifications from YAML:
   - Maps queue names to flow specifications
   - Provides lookup methods for flow resolution

3. **McpGenericHelper** - Unified orchestration service:
   - `validateHeaders()` - Ensures all required headers are present
   - `validateSchema()` - Validates payload against XSD schema
   - `applyTransformation()` - Applies XQuery transformations
   - `routeToAdapter()` - Routes to backend adapter endpoints
   - `processMessage()` - Orchestrates the complete pipeline

4. **OsbPipelineController** - REST API endpoints:
   - `POST /api/osb/process` - Process messages through pipeline
   - `GET /api/osb/flow-spec` - Retrieve flow specifications
   - `GET /api/osb/health` - Health check endpoint

## Configuration

### Flow Configuration (flow-config.yml)

```yaml
flows:
  B2B:
    flowName: B2B
    queueName: Q.B2B.IN
    requiredHeaders:
      - msg-id
      - correlation-id
      - source-system
    xqueryTransformation: /xquery/b2b-transform.xq
    adapterEndpoint: http://adapter-service/b2b
    schemaValidationEnabled: true
    schemaPath: /schemas/b2b-schema.xsd
```

### Supported Flows

1. **B2B** - Business-to-Business integration
2. **Siebel** - Siebel CRM integration
3. **Portal** - Portal system integration
4. **Flow** - Generic flow processing
5. **NEO** - NEO system integration
6. **Sync** - Synchronous processing flow

## API Usage

### Process Message

```bash
curl -X POST http://localhost:8080/api/osb/process \
  -H "x-queue-name: Q.B2B.IN" \
  -H "msg-id: 12345" \
  -H "correlation-id: abc-123" \
  -H "source-system: SAP" \
  -H "Content-Type: application/xml" \
  -d '<message>Hello World</message>'
```

### Get Flow Specification

```bash
curl http://localhost:8080/api/osb/flow-spec?queueName=Q.B2B.IN
```

### Health Check

```bash
curl http://localhost:8080/api/osb/health
```

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Build

```bash
mvn clean install
```

### Run Tests

```bash
mvn test
```

### Run Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/osb-pipeline-orchestration-1.0.0-SNAPSHOT.jar
```

## Testing

The project includes comprehensive unit tests:

- `FlowSpecTest` - Tests for FlowSpec POJO
- `FlowConfigurationTest` - Tests for configuration loading
- `McpGenericHelperTest` - Tests for orchestration logic

Run tests with:
```bash
mvn test
```

## Design Principles

1. **Single Responsibility**: Each component has a clear, focused purpose
2. **Open/Closed**: Easy to add new flows without modifying core logic
3. **Dependency Injection**: Spring manages all component dependencies
4. **Configuration Over Code**: Flow behavior defined in YAML, not code
5. **Fail Fast**: Validates headers and schema early in pipeline
6. **Comprehensive Logging**: Detailed logging for troubleshooting

## Extension Points

To add a new flow:

1. Add flow configuration to `flow-config.yml`
2. No code changes required - the orchestration layer is fully dynamic

To customize behavior:

1. Extend `McpGenericHelper` for custom transformation logic
2. Add custom validators or adapters as Spring beans
3. Inject custom components via Spring dependency injection

## License

MIT License
