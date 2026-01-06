package com.osb.integration.config;

import com.osb.integration.model.FlowSpec;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for loading OSB flow specifications from YAML.
 */
@Component
@ConfigurationProperties(prefix = "flows")
public class FlowConfiguration {
    
    private Map<String, FlowSpec> specs = new HashMap<>();

    public Map<String, FlowSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, FlowSpec> specs) {
        this.specs = specs;
    }

    /**
     * Get FlowSpec by queue name
     */
    public FlowSpec getFlowSpecByQueueName(String queueName) {
        if (specs == null || specs.isEmpty()) {
            return null;
        }
        return specs.values().stream()
                .filter(spec -> spec != null && queueName.equals(spec.getQueueName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get FlowSpec by flow name
     */
    public FlowSpec getFlowSpecByFlowName(String flowName) {
        if (specs == null) {
            return null;
        }
        return specs.get(flowName);
    }
}
