package com.osb.integration.helper;

import com.osb.integration.model.FlowSpec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * McpGenericHelper orchestrates the flow-specific processing logic.
 * It follows OSB semantics with step-by-step processing and logging.
 */
@Component
public class McpGenericHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(McpGenericHelper.class);
    
    /**
     * Processes a message according to the flow specification
     * 
     * @param flowSpec the flow specification
     * @param headers the message headers
     * @param payload the message payload
     * @return the processed result
     * @throws FlowProcessingException if processing fails
     */
    public String processFlow(FlowSpec flowSpec, Map<String, String> headers, String payload) 
            throws FlowProcessingException {
        
        String queueName = flowSpec.getQueueName();
        String correlationId = headers.getOrDefault("x-correlation-id", "unknown");
        
        logger.info("[OSB:{}:{}] Starting flow processing", queueName, correlationId);
        
        try {
            // Step 1: Validate headers
            logger.info("[OSB:{}:{}] Step 1: Validating headers", queueName, correlationId);
            validateHeaders(flowSpec, headers);
            logger.info("[OSB:{}:{}] Header validation successful", queueName, correlationId);
            
            // Step 2: Schema validation (conditional)
            String validatedPayload = payload;
            if (flowSpec.isSchemaValidationEnabled()) {
                logger.info("[OSB:{}:{}] Step 2: Performing schema validation using {}", 
                    queueName, correlationId, flowSpec.getSchemaPath());
                validatedPayload = performSchemaValidation(flowSpec, payload);
                logger.info("[OSB:{}:{}] Schema validation successful", queueName, correlationId);
            } else {
                logger.info("[OSB:{}:{}] Step 2: Schema validation disabled, skipping", 
                    queueName, correlationId);
            }
            
            // Step 3: XQuery transformation (conditional)
            String transformedPayload = validatedPayload;
            if (flowSpec.isTransformationEnabled()) {
                logger.info("[OSB:{}:{}] Step 3: Performing XQuery transformation using {}", 
                    queueName, correlationId, flowSpec.getTransformationPath());
                transformedPayload = performTransformation(flowSpec, validatedPayload);
                logger.info("[OSB:{}:{}] Transformation successful", queueName, correlationId);
            } else {
                logger.info("[OSB:{}:{}] Step 3: Transformation disabled, skipping", 
                    queueName, correlationId);
            }
            
            // Step 4: Call adapter endpoint
            logger.info("[OSB:{}:{}] Step 4: Invoking adapter endpoint: {}", 
                queueName, correlationId, flowSpec.getAdapterEndpoint());
            String response = callAdapterEndpoint(flowSpec, headers, transformedPayload);
            logger.info("[OSB:{}:{}] Adapter call successful", queueName, correlationId);
            
            logger.info("[OSB:{}:{}] Flow processing completed successfully", queueName, correlationId);
            return response;
            
        } catch (Exception e) {
            logger.error("[OSB:{}:{}] Flow processing failed: {}", 
                queueName, correlationId, e.getMessage(), e);
            throw new FlowProcessingException("Flow processing failed for " + queueName, e);
        }
    }
    
    /**
     * Validates that all required headers are present
     * 
     * @param flowSpec the flow specification
     * @param headers the message headers
     * @throws FlowProcessingException if validation fails
     */
    private void validateHeaders(FlowSpec flowSpec, Map<String, String> headers) 
            throws FlowProcessingException {
        
        List<String> requiredHeaders = flowSpec.getRequiredHeaders();
        if (requiredHeaders == null || requiredHeaders.isEmpty()) {
            return;
        }
        
        for (String headerName : requiredHeaders) {
            String headerValue = headers.get(headerName);
            if (StringUtils.isBlank(headerValue)) {
                String errorMsg = String.format("Required header '%s' is missing or empty", headerName);
                logger.error("[OSB:{}] Header validation failed: {}", 
                    flowSpec.getQueueName(), errorMsg);
                throw new FlowProcessingException(errorMsg);
            }
        }
    }
    
    /**
     * Performs schema validation on the payload
     * 
     * @param flowSpec the flow specification
     * @param payload the message payload
     * @return the validated payload
     * @throws FlowProcessingException if validation fails
     */
    private String performSchemaValidation(FlowSpec flowSpec, String payload) 
            throws FlowProcessingException {
        
        // Simulate schema validation
        // In a real implementation, this would use an XML validator with the XSD schema
        if (StringUtils.isBlank(payload)) {
            throw new FlowProcessingException("Payload is empty, schema validation failed");
        }
        
        logger.debug("[OSB:{}] Schema validation performed against {}", 
            flowSpec.getQueueName(), flowSpec.getSchemaPath());
        
        // Return payload as-is after validation
        return payload;
    }
    
    /**
     * Performs XQuery transformation on the payload
     * 
     * @param flowSpec the flow specification
     * @param payload the message payload
     * @return the transformed payload
     * @throws FlowProcessingException if transformation fails
     */
    private String performTransformation(FlowSpec flowSpec, String payload) 
            throws FlowProcessingException {
        
        // Simulate XQuery transformation
        // In a real implementation, this would execute the XQuery script
        if (StringUtils.isBlank(payload)) {
            throw new FlowProcessingException("Payload is empty, transformation failed");
        }
        
        logger.debug("[OSB:{}] Transformation performed using {}", 
            flowSpec.getQueueName(), flowSpec.getTransformationPath());
        
        // Return transformed payload (simulated by adding a tag)
        String transformed = String.format("<transformed flow=\"%s\">%s</transformed>", 
            flowSpec.getQueueName(), payload);
        return transformed;
    }
    
    /**
     * Calls the adapter endpoint with the processed payload
     * 
     * @param flowSpec the flow specification
     * @param headers the message headers
     * @param payload the processed payload
     * @return the adapter response
     * @throws FlowProcessingException if the call fails
     */
    private String callAdapterEndpoint(FlowSpec flowSpec, Map<String, String> headers, String payload) 
            throws FlowProcessingException {
        
        // Simulate adapter endpoint call
        // In a real implementation, this would use RestTemplate or WebClient
        String endpoint = flowSpec.getAdapterEndpoint();
        if (StringUtils.isBlank(endpoint)) {
            throw new FlowProcessingException("Adapter endpoint is not configured");
        }
        
        logger.debug("[OSB:{}] Calling adapter endpoint: {}", 
            flowSpec.getQueueName(), endpoint);
        
        // Return simulated response
        String response = String.format("<response><status>success</status><flow>%s</flow></response>", 
            flowSpec.getQueueName());
        return response;
    }
}
