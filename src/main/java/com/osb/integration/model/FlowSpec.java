package com.osb.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FlowSpec models the configuration for a specific OSB flow.
 * Each flow has specific requirements for validation, transformation, and adapter endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowSpec {
    
    /**
     * The queue name that identifies this flow (e.g., "B2B", "Siebel", "Portal", etc.)
     */
    private String queueName;
    
    /**
     * Human-readable description of the flow
     */
    private String description;
    
    /**
     * List of required headers for this flow
     */
    private List<String> requiredHeaders;
    
    /**
     * Flag indicating whether schema validation should be performed
     */
    private boolean schemaValidationEnabled;
    
    /**
     * Path to the XSD schema file (if schema validation is enabled)
     */
    private String schemaPath;
    
    /**
     * Flag indicating whether XQuery transformation should be performed
     */
    private boolean transformationEnabled;
    
    /**
     * Path to the XQuery transformation file (if transformation is enabled)
     */
    private String transformationPath;
    
    /**
     * The adapter endpoint URL to call after processing
     */
    private String adapterEndpoint;
    
    /**
     * Additional flow-specific properties
     */
    private String properties;
}
