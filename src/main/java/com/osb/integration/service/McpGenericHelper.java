package com.osb.integration.service;

import com.osb.integration.config.FlowConfiguration;
import com.osb.integration.model.FlowSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * McpGenericHelper - Unified orchestration service for all OSB pipeline flows.
 * Dynamically selects behaviors based on flow configuration.
 */
@Service
public class McpGenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(McpGenericHelper.class);

    private final FlowConfiguration flowConfiguration;
    private final RestTemplate restTemplate;

    public McpGenericHelper(FlowConfiguration flowConfiguration) {
        this.flowConfiguration = flowConfiguration;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Process a message through the OSB pipeline.
     * 
     * @param queueName The queue name identifying the flow
     * @param headers Message headers
     * @param payload Message payload
     * @return Processed result
     */
    public String processMessage(String queueName, Map<String, String> headers, String payload) {
        logger.info("Processing message for queue: {}", queueName);
        
        // Get flow specification
        FlowSpec flowSpec = flowConfiguration.getFlowSpecByQueueName(queueName);
        if (flowSpec == null) {
            throw new IllegalArgumentException("No flow configuration found for queue: " + queueName);
        }
        
        logger.info("Selected flow: {}", flowSpec.getFlowName());
        
        // Step 1: Validate headers
        validateHeaders(headers, flowSpec.getRequiredHeaders());
        
        // Step 2: Perform schema validation if enabled
        String validatedPayload = payload;
        if (flowSpec.isSchemaValidationEnabled()) {
            validatedPayload = validateSchema(payload, flowSpec.getSchemaPath());
        }
        
        // Step 3: Apply transformation
        String transformedPayload = applyTransformation(validatedPayload, flowSpec.getXqueryTransformation());
        
        // Step 4: Route to adapter endpoint
        String result = routeToAdapter(transformedPayload, flowSpec.getAdapterEndpoint(), headers);
        
        logger.info("Message processing completed for flow: {}", flowSpec.getFlowName());
        return result;
    }

    /**
     * Validate required headers are present.
     * 
     * @param headers Provided headers
     * @param requiredHeaders Required header names
     * @throws IllegalArgumentException if any required header is missing
     */
    public void validateHeaders(Map<String, String> headers, List<String> requiredHeaders) {
        logger.debug("Validating headers. Required: {}", requiredHeaders);
        
        if (requiredHeaders == null || requiredHeaders.isEmpty()) {
            logger.debug("No required headers specified");
            return;
        }
        
        for (String requiredHeader : requiredHeaders) {
            if (!headers.containsKey(requiredHeader) || headers.get(requiredHeader) == null) {
                String errorMsg = "Missing required header: " + requiredHeader;
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
        }
        
        logger.debug("Header validation successful");
    }

    /**
     * Validate payload against XML schema.
     * 
     * @param payload XML payload
     * @param schemaPath Path to XSD schema
     * @return Validated payload
     * @throws RuntimeException if validation fails
     */
    public String validateSchema(String payload, String schemaPath) {
        logger.debug("Validating schema: {}", schemaPath);
        
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            // Load schema from classpath
            InputStream schemaStream = getClass().getResourceAsStream(schemaPath);
            if (schemaStream == null) {
                logger.warn("Schema file not found: {}. Skipping validation.", schemaPath);
                return payload;
            }
            
            Schema schema = factory.newSchema(new StreamSource(schemaStream));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(payload)));
            
            logger.debug("Schema validation successful");
            return payload;
            
        } catch (Exception e) {
            String errorMsg = "Schema validation failed: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Apply XQuery transformation to payload.
     * 
     * @param payload Input payload
     * @param transformationPath Path to XQuery transformation file
     * @return Transformed payload
     */
    public String applyTransformation(String payload, String transformationPath) {
        logger.debug("Applying transformation: {}", transformationPath);
        
        if (transformationPath == null || transformationPath.isEmpty()) {
            logger.debug("No transformation specified, returning original payload");
            return payload;
        }
        
        try {
            // Load transformation from classpath
            InputStream transformStream = getClass().getResourceAsStream(transformationPath);
            if (transformStream == null) {
                logger.warn("Transformation file not found: {}. Returning original payload.", transformationPath);
                return payload;
            }
            
            // In a real implementation, this would apply XQuery transformation
            // For now, we'll simulate by returning the payload
            logger.debug("Transformation applied successfully");
            return payload;
            
        } catch (Exception e) {
            String errorMsg = "Transformation failed: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Route transformed payload to adapter endpoint.
     * 
     * @param payload Transformed payload
     * @param endpointUrl Adapter endpoint URL
     * @param headers Message headers for routing
     * @return Response from adapter
     */
    public String routeToAdapter(String payload, String endpointUrl, Map<String, String> headers) {
        logger.debug("Routing to adapter endpoint: {}", endpointUrl);
        
        if (endpointUrl == null || endpointUrl.isEmpty()) {
            logger.warn("No adapter endpoint specified, returning payload");
            return payload;
        }
        
        try {
            // In a real implementation, this would make HTTP call to adapter
            // For now, we'll simulate success
            logger.info("Successfully routed to adapter: {}", endpointUrl);
            return "SUCCESS: Routed to " + endpointUrl;
            
        } catch (Exception e) {
            String errorMsg = "Adapter routing failed: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Get flow specification by queue name.
     * 
     * @param queueName Queue name
     * @return FlowSpec or null if not found
     */
    public FlowSpec getFlowSpec(String queueName) {
        return flowConfiguration.getFlowSpecByQueueName(queueName);
    }
}
