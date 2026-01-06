package com.osb.integration.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.osb.integration.model.FlowSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlowRegistry loads and manages flow configurations from YAML.
 * It provides a mapping from x-queue-name to FlowSpec.
 */
@Component
public class FlowRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(FlowRegistry.class);
    private static final String FLOW_CONFIG_FILE = "flow-config.yml";
    
    private final Map<String, FlowSpec> flowSpecMap = new HashMap<>();
    
    /**
     * Loads flow configurations from YAML on startup
     */
    @PostConstruct
    public void initialize() {
        try {
            logger.info("Loading flow configurations from {}", FLOW_CONFIG_FILE);
            loadFlowConfigurations();
            logger.info("Successfully loaded {} flow configurations", flowSpecMap.size());
        } catch (IOException e) {
            logger.error("Failed to load flow configurations", e);
            throw new RuntimeException("Failed to initialize FlowRegistry", e);
        }
    }
    
    /**
     * Loads flow configurations from YAML file
     */
    private void loadFlowConfigurations() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ClassPathResource resource = new ClassPathResource(FLOW_CONFIG_FILE);
        
        try (InputStream inputStream = resource.getInputStream()) {
            FlowConfigWrapper wrapper = mapper.readValue(inputStream, FlowConfigWrapper.class);
            
            for (FlowSpec flowSpec : wrapper.getFlows()) {
                flowSpecMap.put(flowSpec.getQueueName(), flowSpec);
                logger.debug("Registered flow: {} - {}", flowSpec.getQueueName(), flowSpec.getDescription());
            }
        }
    }
    
    /**
     * Retrieves a FlowSpec by queue name
     * 
     * @param queueName the x-queue-name header value
     * @return the FlowSpec or null if not found
     */
    public FlowSpec getFlowSpec(String queueName) {
        FlowSpec flowSpec = flowSpecMap.get(queueName);
        if (flowSpec == null) {
            logger.warn("No flow configuration found for queue: {}", queueName);
        }
        return flowSpec;
    }
    
    /**
     * Checks if a flow exists for the given queue name
     * 
     * @param queueName the x-queue-name header value
     * @return true if flow exists, false otherwise
     */
    public boolean hasFlow(String queueName) {
        return flowSpecMap.containsKey(queueName);
    }
    
    /**
     * Returns all registered flow names
     * 
     * @return map of all flow specifications
     */
    public Map<String, FlowSpec> getAllFlows() {
        return new HashMap<>(flowSpecMap);
    }
    
    /**
     * Wrapper class for YAML deserialization
     */
    private static class FlowConfigWrapper {
        private List<FlowSpec> flows;
        
        public List<FlowSpec> getFlows() {
            return flows;
        }
        
        public void setFlows(List<FlowSpec> flows) {
            this.flows = flows;
        }
    }
}
