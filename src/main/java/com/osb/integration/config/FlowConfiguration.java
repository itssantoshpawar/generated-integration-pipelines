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
@ConfigurationProperties
public class FlowConfiguration {
    
    private Map<String, FlowSpec> flows = new HashMap<>();

    public Map<String, FlowSpec> getFlows() {
        return flows;
    }

    public void setFlows(Map<String, FlowSpec> flows) {
        this.flows = flows;
    }

    /**
     * Get FlowSpec by queue name
     */
    public FlowSpec getFlowSpecByQueueName(String queueName) {
        if (flows == null || flows.isEmpty()) {
            return null;
        }
        return flows.values().stream()
                .filter(spec -> spec != null && queueName.equals(spec.getQueueName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get FlowSpec by flow name
     */
    public FlowSpec getFlowSpecByFlowName(String flowName) {
        if (flows == null) {
            return null;
        }
        return flows.get(flowName);
    }
}
