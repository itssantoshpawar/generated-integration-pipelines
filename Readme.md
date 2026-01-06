# OSB Flow Framework

A generic framework for handling Oracle Service Bus (OSB) flows with configurable validation, transformation, and adapter integration.

## Overview

This framework provides a flexible, configuration-driven approach to processing integration flows. It supports 6 different flow types (B2B, Siebel, Portal, Flow, NEO, Sync) with customizable processing rules for each.

## Architecture

### Components

1. **FlowSpec**: POJO that models flow-specific configurations
2. **FlowRegistry**: Manages flow configurations loaded from YAML
3. **McpGenericHelper**: Orchestrates flow-specific processing logic
4. **Mcpt2rService**: Service layer that routes messages to appropriate flows

### Flow Processing Pipeline

Each message goes through the following steps:

1. **Header Validation**: Validates required headers based on flow configuration
2. **Schema Validation** (conditional): Validates payload against XSD schema
3. **XQuery Transformation** (conditional): Transforms payload using XQuery
4. **Adapter Call**: Invokes the configured adapter endpoint

All steps follow OSB semantics with detailed step-by-step logging.

## Supported Flows

| Flow | Schema Validation | Transformation | Required Headers |
|------|------------------|----------------|------------------|
| B2B | ✅ | ✅ | x-queue-name, x-correlation-id, x-message-type |
| Siebel | ✅ | ✅ | x-queue-name, x-correlation-id, x-session-id |
| Portal | ❌ | ✅ | x-queue-name, x-correlation-id, x-user-id |
| Flow | ✅ | ❌ | x-queue-name, x-correlation-id |
| NEO | ✅ | ✅ | x-queue-name, x-correlation-id, x-platform-id |
| Sync | ❌ | ❌ | x-queue-name, x-correlation-id, x-sync-mode |

## Configuration

Flows are configured in `src/main/resources/flow-config.yml`:

```yaml
flows:
  - queueName: "B2B"
    description: "B2B Integration Flow"
    requiredHeaders:
      - "x-queue-name"
      - "x-correlation-id"
      - "x-message-type"
    schemaValidationEnabled: true
    schemaPath: "/schemas/b2b-schema.xsd"
    transformationEnabled: true
    transformationPath: "/xquery/b2b-transform.xq"
    adapterEndpoint: "http://adapter-service/b2b/process"
    properties: "flow=B2B;priority=high"
```

## Usage

### Processing a Message

```java
@Autowired
private Mcpt2rService mcpt2rService;

public void processMessage() {
    Map<String, String> headers = new HashMap<>();
    headers.put("x-queue-name", "B2B");
    headers.put("x-correlation-id", "corr-123");
    headers.put("x-message-type", "order");
    
    String payload = "<order>...</order>";
    
    try {
        String response = mcpt2rService.processMessage(headers, payload);
        // Handle response
    } catch (FlowProcessingException e) {
        // Handle error
    }
}
```

### Adding a New Flow

1. Add flow configuration to `flow-config.yml`
2. Restart the application (FlowRegistry loads on startup)
3. No code changes required!

## Building and Testing

### Build the project
```bash
mvn clean compile
```

### Run tests
```bash
mvn test
```

### Run the application
```bash
mvn spring-boot:run
```

## Test Coverage

The framework includes 42 comprehensive unit tests covering:

- ✅ Valid/missing headers for each flow
- ✅ Schema validation toggles
- ✅ Transformation execution
- ✅ Adapter endpoint calls
- ✅ OSB-style logging verification
- ✅ Error handling scenarios

## Logging

The framework provides OSB-style structured logging:

```
[OSB:B2B:corr-123] Starting flow processing
[OSB:B2B:corr-123] Step 1: Validating headers
[OSB:B2B:corr-123] Header validation successful
[OSB:B2B:corr-123] Step 2: Performing schema validation using /schemas/b2b-schema.xsd
[OSB:B2B:corr-123] Schema validation successful
[OSB:B2B:corr-123] Step 3: Performing XQuery transformation using /xquery/b2b-transform.xq
[OSB:B2B:corr-123] Transformation successful
[OSB:B2B:corr-123] Step 4: Invoking adapter endpoint: http://adapter-service/b2b/process
[OSB:B2B:corr-123] Adapter call successful
[OSB:B2B:corr-123] Flow processing completed successfully
```

## Requirements

- Java 11+
- Maven 3.6+
- Spring Boot 2.7.14

## License

This project is licensed under the MIT License.
