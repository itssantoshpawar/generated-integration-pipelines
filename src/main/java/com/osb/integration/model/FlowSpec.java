package com.osb.integration.model;

import java.util.List;
import java.util.Map;

/**
 * FlowSpec POJO representing metadata about OSB pipeline flows.
 * Maps configuration for each flow type (B2B, Siebel, Portal, Flow, NEO, Sync).
 */
public class FlowSpec {
    
    private String flowName;
    private String queueName;
    private List<String> requiredHeaders;
    private String xqueryTransformation;
    private String adapterEndpoint;
    private boolean schemaValidationEnabled;
    private String schemaPath;
    private Map<String, String> additionalProperties;

    public FlowSpec() {
    }

    public FlowSpec(String flowName, String queueName, List<String> requiredHeaders) {
        this.flowName = flowName;
        this.queueName = queueName;
        this.requiredHeaders = requiredHeaders;
    }

    // Getters and Setters
    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public List<String> getRequiredHeaders() {
        return requiredHeaders;
    }

    public void setRequiredHeaders(List<String> requiredHeaders) {
        this.requiredHeaders = requiredHeaders;
    }

    public String getXqueryTransformation() {
        return xqueryTransformation;
    }

    public void setXqueryTransformation(String xqueryTransformation) {
        this.xqueryTransformation = xqueryTransformation;
    }

    public String getAdapterEndpoint() {
        return adapterEndpoint;
    }

    public void setAdapterEndpoint(String adapterEndpoint) {
        this.adapterEndpoint = adapterEndpoint;
    }

    public boolean isSchemaValidationEnabled() {
        return schemaValidationEnabled;
    }

    public void setSchemaValidationEnabled(boolean schemaValidationEnabled) {
        this.schemaValidationEnabled = schemaValidationEnabled;
    }

    public String getSchemaPath() {
        return schemaPath;
    }

    public void setSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
    }

    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        return "FlowSpec{" +
                "flowName='" + flowName + '\'' +
                ", queueName='" + queueName + '\'' +
                ", requiredHeaders=" + requiredHeaders +
                ", xqueryTransformation='" + xqueryTransformation + '\'' +
                ", adapterEndpoint='" + adapterEndpoint + '\'' +
                ", schemaValidationEnabled=" + schemaValidationEnabled +
                ", schemaPath='" + schemaPath + '\'' +
                '}';
    }
}
