package com.osb.integration.controller;

import com.osb.integration.model.FlowSpec;
import com.osb.integration.service.McpGenericHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for OSB Pipeline Orchestration.
 */
@RestController
@RequestMapping("/api/osb")
public class OsbPipelineController {

    private static final Logger logger = LoggerFactory.getLogger(OsbPipelineController.class);

    private final McpGenericHelper mcpGenericHelper;

    public OsbPipelineController(McpGenericHelper mcpGenericHelper) {
        this.mcpGenericHelper = mcpGenericHelper;
    }

    /**
     * Sanitize error message to prevent information disclosure.
     * Only exposes safe, controlled error information using a whitelist approach.
     */
    private String sanitizeValidationError(String errorMessage) {
        if (errorMessage == null) {
            return "Invalid request parameters";
        }
        
        // Whitelist of allowed header names
        String[] allowedHeaders = {"msg-id", "correlation-id", "source-system", 
                                   "siebel-operation", "user-id", "flow-type", 
                                   "neo-transaction-id", "sync-mode"};
        
        // Handle missing header errors - only expose if header is in whitelist
        if (errorMessage.startsWith("Missing required header: ")) {
            String headerName = errorMessage.substring("Missing required header: ".length());
            for (String allowedHeader : allowedHeaders) {
                if (allowedHeader.equals(headerName)) {
                    return "Missing required header: " + allowedHeader;
                }
            }
            return "Missing required header";
        }
        
        // Handle invalid queue name
        if (errorMessage.contains("No flow configuration found")) {
            return "Invalid or unsupported queue name";
        }
        
        // Default safe message
        return "Invalid request parameters";
    }

    /**
     * Process a message through the OSB pipeline.
     * 
     * @param queueName Queue name header
     * @param headers All request headers
     * @param payload Message payload
     * @return Processing result
     */
    @PostMapping("/process")
    public ResponseEntity<String> processMessage(
            @RequestHeader("x-queue-name") String queueName,
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload) {
        
        try {
            logger.info("Received message for queue: {}", queueName);
            String result = mcpGenericHelper.processMessage(queueName, headers, payload);
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            String safeMessage = sanitizeValidationError(e.getMessage());
            return ResponseEntity.badRequest().body("Validation Error: " + safeMessage);
            
        } catch (Exception e) {
            logger.error("Processing error: {}", e.getMessage(), e);
            // Don't expose internal error details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Processing Error: Unable to process message");
        }
    }

    /**
     * Get flow specification by queue name.
     * 
     * @param queueName Queue name
     * @return FlowSpec details
     */
    @GetMapping("/flow-spec")
    public ResponseEntity<FlowSpec> getFlowSpec(@RequestParam String queueName) {
        try {
            FlowSpec flowSpec = mcpGenericHelper.getFlowSpec(queueName);
            if (flowSpec == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(flowSpec);
            
        } catch (Exception e) {
            logger.error("Error retrieving flow spec: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check endpoint.
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OSB Pipeline Orchestration Service is running");
    }
}
