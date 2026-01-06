package com.osb.integration.helper;

import com.osb.integration.model.FlowSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for McpGenericHelper covering all 6 flows
 */
@ExtendWith(MockitoExtension.class)
class McpGenericHelperTest {
    
    @InjectMocks
    private McpGenericHelper helper;
    
    private Map<String, String> headers;
    private String payload;
    
    @BeforeEach
    void setUp() {
        headers = new HashMap<>();
        payload = "<message>test payload</message>";
    }
    
    // ========== B2B Flow Tests ==========
    
    @Test
    void testB2BFlow_WithValidHeaders_ShouldSucceed() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createB2BFlowSpec();
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "corr-123");
        headers.put("x-message-type", "order");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("B2B"));
    }
    
    @Test
    void testB2BFlow_WithMissingHeaders_ShouldFail() {
        // Arrange
        FlowSpec flowSpec = createB2BFlowSpec();
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "corr-123");
        // Missing x-message-type
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class, 
            () -> helper.processFlow(flowSpec, headers, payload));
        // Check the cause message since the wrapper message just says "Flow processing failed for B2B"
        String errorMsg = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        assertTrue(errorMsg.contains("x-message-type"), "Expected error about x-message-type but got: " + errorMsg);
    }
    
    @Test
    void testB2BFlow_WithSchemaValidation_ShouldExecute() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createB2BFlowSpec();
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "corr-123");
        headers.put("x-message-type", "order");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        // Schema validation is enabled for B2B
        assertTrue(flowSpec.isSchemaValidationEnabled());
    }
    
    @Test
    void testB2BFlow_WithTransformation_ShouldTransform() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createB2BFlowSpec();
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "corr-123");
        headers.put("x-message-type", "order");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        // Transformation is enabled for B2B
        assertTrue(flowSpec.isTransformationEnabled());
    }
    
    // ========== Siebel Flow Tests ==========
    
    @Test
    void testSiebelFlow_WithValidHeaders_ShouldSucceed() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createSiebelFlowSpec();
        headers.put("x-queue-name", "Siebel");
        headers.put("x-correlation-id", "corr-456");
        headers.put("x-session-id", "session-789");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("Siebel"));
    }
    
    @Test
    void testSiebelFlow_WithMissingSessionId_ShouldFail() {
        // Arrange
        FlowSpec flowSpec = createSiebelFlowSpec();
        headers.put("x-queue-name", "Siebel");
        headers.put("x-correlation-id", "corr-456");
        // Missing x-session-id
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class, 
            () -> helper.processFlow(flowSpec, headers, payload));
        String errorMsg = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        assertTrue(errorMsg.contains("x-session-id"), "Expected error about x-session-id but got: " + errorMsg);
    }
    
    @Test
    void testSiebelFlow_SchemaValidationEnabled() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createSiebelFlowSpec();
        headers.put("x-queue-name", "Siebel");
        headers.put("x-correlation-id", "corr-456");
        headers.put("x-session-id", "session-789");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(flowSpec.isSchemaValidationEnabled());
        assertTrue(flowSpec.isTransformationEnabled());
    }
    
    // ========== Portal Flow Tests ==========
    
    @Test
    void testPortalFlow_WithValidHeaders_ShouldSucceed() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createPortalFlowSpec();
        headers.put("x-queue-name", "Portal");
        headers.put("x-correlation-id", "corr-789");
        headers.put("x-user-id", "user-123");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("Portal"));
    }
    
    @Test
    void testPortalFlow_SchemaValidationDisabled() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createPortalFlowSpec();
        headers.put("x-queue-name", "Portal");
        headers.put("x-correlation-id", "corr-789");
        headers.put("x-user-id", "user-123");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        // Schema validation is disabled for Portal
        assertFalse(flowSpec.isSchemaValidationEnabled());
        // But transformation is enabled
        assertTrue(flowSpec.isTransformationEnabled());
    }
    
    @Test
    void testPortalFlow_WithMissingUserId_ShouldFail() {
        // Arrange
        FlowSpec flowSpec = createPortalFlowSpec();
        headers.put("x-queue-name", "Portal");
        headers.put("x-correlation-id", "corr-789");
        // Missing x-user-id
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class, 
            () -> helper.processFlow(flowSpec, headers, payload));
        String errorMsg = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        assertTrue(errorMsg.contains("x-user-id"), "Expected error about x-user-id but got: " + errorMsg);
    }
    
    // ========== Flow (Generic) Tests ==========
    
    @Test
    void testGenericFlow_WithValidHeaders_ShouldSucceed() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createGenericFlowSpec();
        headers.put("x-queue-name", "Flow");
        headers.put("x-correlation-id", "corr-111");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("Flow"));
    }
    
    @Test
    void testGenericFlow_TransformationDisabled() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createGenericFlowSpec();
        headers.put("x-queue-name", "Flow");
        headers.put("x-correlation-id", "corr-111");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        // Schema validation is enabled
        assertTrue(flowSpec.isSchemaValidationEnabled());
        // But transformation is disabled
        assertFalse(flowSpec.isTransformationEnabled());
    }
    
    // ========== NEO Flow Tests ==========
    
    @Test
    void testNEOFlow_WithValidHeaders_ShouldSucceed() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createNEOFlowSpec();
        headers.put("x-queue-name", "NEO");
        headers.put("x-correlation-id", "corr-222");
        headers.put("x-platform-id", "platform-cloud");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("NEO"));
    }
    
    @Test
    void testNEOFlow_WithMissingPlatformId_ShouldFail() {
        // Arrange
        FlowSpec flowSpec = createNEOFlowSpec();
        headers.put("x-queue-name", "NEO");
        headers.put("x-correlation-id", "corr-222");
        // Missing x-platform-id
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class, 
            () -> helper.processFlow(flowSpec, headers, payload));
        String errorMsg = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        assertTrue(errorMsg.contains("x-platform-id"), "Expected error about x-platform-id but got: " + errorMsg);
    }
    
    @Test
    void testNEOFlow_BothValidationAndTransformationEnabled() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createNEOFlowSpec();
        headers.put("x-queue-name", "NEO");
        headers.put("x-correlation-id", "corr-222");
        headers.put("x-platform-id", "platform-cloud");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(flowSpec.isSchemaValidationEnabled());
        assertTrue(flowSpec.isTransformationEnabled());
    }
    
    // ========== Sync Flow Tests ==========
    
    @Test
    void testSyncFlow_WithValidHeaders_ShouldSucceed() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createSyncFlowSpec();
        headers.put("x-queue-name", "Sync");
        headers.put("x-correlation-id", "corr-333");
        headers.put("x-sync-mode", "synchronous");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("Sync"));
    }
    
    @Test
    void testSyncFlow_BothValidationAndTransformationDisabled() throws FlowProcessingException {
        // Arrange
        FlowSpec flowSpec = createSyncFlowSpec();
        headers.put("x-queue-name", "Sync");
        headers.put("x-correlation-id", "corr-333");
        headers.put("x-sync-mode", "synchronous");
        
        // Act
        String result = helper.processFlow(flowSpec, headers, payload);
        
        // Assert
        assertNotNull(result);
        // Both validation and transformation are disabled for Sync
        assertFalse(flowSpec.isSchemaValidationEnabled());
        assertFalse(flowSpec.isTransformationEnabled());
    }
    
    @Test
    void testSyncFlow_WithMissingSyncMode_ShouldFail() {
        // Arrange
        FlowSpec flowSpec = createSyncFlowSpec();
        headers.put("x-queue-name", "Sync");
        headers.put("x-correlation-id", "corr-333");
        // Missing x-sync-mode
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class, 
            () -> helper.processFlow(flowSpec, headers, payload));
        String errorMsg = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        assertTrue(errorMsg.contains("x-sync-mode"), "Expected error about x-sync-mode but got: " + errorMsg);
    }
    
    // ========== Edge Cases ==========
    
    @Test
    void testFlow_WithEmptyPayload_ShouldFail() {
        // Arrange
        FlowSpec flowSpec = createB2BFlowSpec();
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "corr-123");
        headers.put("x-message-type", "order");
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class, 
            () -> helper.processFlow(flowSpec, headers, ""));
        String errorMsg = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        assertTrue(errorMsg.contains("empty"), "Expected error about empty payload but got: " + errorMsg);
    }
    
    @Test
    void testFlow_WithEmptyHeaderValue_ShouldFail() {
        // Arrange
        FlowSpec flowSpec = createB2BFlowSpec();
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "");
        headers.put("x-message-type", "order");
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class, 
            () -> helper.processFlow(flowSpec, headers, payload));
        String errorMsg = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        assertTrue(errorMsg.contains("x-correlation-id"), "Expected error about x-correlation-id but got: " + errorMsg);
    }
    
    // ========== Helper Methods to Create FlowSpecs ==========
    
    private FlowSpec createB2BFlowSpec() {
        return FlowSpec.builder()
            .queueName("B2B")
            .description("B2B Integration Flow")
            .requiredHeaders(Arrays.asList("x-queue-name", "x-correlation-id", "x-message-type"))
            .schemaValidationEnabled(true)
            .schemaPath("/schemas/b2b-schema.xsd")
            .transformationEnabled(true)
            .transformationPath("/xquery/b2b-transform.xq")
            .adapterEndpoint("http://adapter-service/b2b/process")
            .properties("flow=B2B;priority=high")
            .build();
    }
    
    private FlowSpec createSiebelFlowSpec() {
        return FlowSpec.builder()
            .queueName("Siebel")
            .description("Siebel CRM Integration Flow")
            .requiredHeaders(Arrays.asList("x-queue-name", "x-correlation-id", "x-session-id"))
            .schemaValidationEnabled(true)
            .schemaPath("/schemas/siebel-schema.xsd")
            .transformationEnabled(true)
            .transformationPath("/xquery/siebel-transform.xq")
            .adapterEndpoint("http://adapter-service/siebel/process")
            .properties("flow=Siebel;crm=true")
            .build();
    }
    
    private FlowSpec createPortalFlowSpec() {
        return FlowSpec.builder()
            .queueName("Portal")
            .description("Portal Integration Flow")
            .requiredHeaders(Arrays.asList("x-queue-name", "x-correlation-id", "x-user-id"))
            .schemaValidationEnabled(false)
            .schemaPath("")
            .transformationEnabled(true)
            .transformationPath("/xquery/portal-transform.xq")
            .adapterEndpoint("http://adapter-service/portal/process")
            .properties("flow=Portal;web=true")
            .build();
    }
    
    private FlowSpec createGenericFlowSpec() {
        return FlowSpec.builder()
            .queueName("Flow")
            .description("Generic Flow Integration")
            .requiredHeaders(Arrays.asList("x-queue-name", "x-correlation-id"))
            .schemaValidationEnabled(true)
            .schemaPath("/schemas/flow-schema.xsd")
            .transformationEnabled(false)
            .transformationPath("")
            .adapterEndpoint("http://adapter-service/flow/process")
            .properties("flow=Generic")
            .build();
    }
    
    private FlowSpec createNEOFlowSpec() {
        return FlowSpec.builder()
            .queueName("NEO")
            .description("NEO Platform Integration Flow")
            .requiredHeaders(Arrays.asList("x-queue-name", "x-correlation-id", "x-platform-id"))
            .schemaValidationEnabled(true)
            .schemaPath("/schemas/neo-schema.xsd")
            .transformationEnabled(true)
            .transformationPath("/xquery/neo-transform.xq")
            .adapterEndpoint("http://adapter-service/neo/process")
            .properties("flow=NEO;platform=cloud")
            .build();
    }
    
    private FlowSpec createSyncFlowSpec() {
        return FlowSpec.builder()
            .queueName("Sync")
            .description("Synchronous Integration Flow")
            .requiredHeaders(Arrays.asList("x-queue-name", "x-correlation-id", "x-sync-mode"))
            .schemaValidationEnabled(false)
            .schemaPath("")
            .transformationEnabled(false)
            .transformationPath("")
            .adapterEndpoint("http://adapter-service/sync/process")
            .properties("flow=Sync;mode=synchronous")
            .build();
    }
}
