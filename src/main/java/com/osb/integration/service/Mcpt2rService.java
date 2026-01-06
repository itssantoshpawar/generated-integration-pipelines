package com.osb.integration.service;

import com.osb.integration.helper.FlowProcessingException;
import com.osb.integration.helper.McpGenericHelper;
import com.osb.integration.model.FlowSpec;
import com.osb.integration.registry.FlowRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Mcpt2rService handles message processing by delegating to McpGenericHelper
 * based on the x-queue-name header.
 */
@Service
public class Mcpt2rService {
    
    private static final Logger logger = LoggerFactory.getLogger(Mcpt2rService.class);
    private static final String QUEUE_NAME_HEADER = "x-queue-name";
    
    private final FlowRegistry flowRegistry;
    private final McpGenericHelper genericHelper;
    
    @Autowired
    public Mcpt2rService(FlowRegistry flowRegistry, McpGenericHelper genericHelper) {
        this.flowRegistry = flowRegistry;
        this.genericHelper = genericHelper;
    }
    
    /**
     * Processes a message by routing it to the appropriate flow based on x-queue-name
     * 
     * @param headers the message headers
     * @param payload the message payload
     * @return the processing result
     * @throws FlowProcessingException if processing fails
     */
    public String processMessage(Map<String, String> headers, String payload) 
            throws FlowProcessingException {
        
        // Extract queue name from headers
        String queueName = headers.get(QUEUE_NAME_HEADER);
        if (queueName == null || queueName.trim().isEmpty()) {
            logger.error("Missing or empty {} header", QUEUE_NAME_HEADER);
            throw new FlowProcessingException("Missing or empty x-queue-name header");
        }
        
        logger.info("Processing message for queue: {}", queueName);
        
        // Get flow specification
        FlowSpec flowSpec = flowRegistry.getFlowSpec(queueName);
        if (flowSpec == null) {
            logger.error("No flow configuration found for queue: {}", queueName);
            throw new FlowProcessingException("Unknown queue name: " + queueName);
        }
        
        // Delegate to generic helper
        return genericHelper.processFlow(flowSpec, headers, payload);
    }
}
