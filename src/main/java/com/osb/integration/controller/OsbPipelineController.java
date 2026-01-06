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
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
            
        } catch (Exception e) {
            logger.error("Processing error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Processing Error: " + e.getMessage());
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
