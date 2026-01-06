package com.osb.integration.registry;

import com.osb.integration.model.FlowSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for FlowRegistry that loads actual YAML configuration
 */
@SpringBootTest(classes = {FlowRegistry.class})
@TestPropertySource(locations = "classpath:application.properties")
class FlowRegistryTest {
    
    @Autowired
    private FlowRegistry flowRegistry;
    
    @BeforeEach
    void setUp() {
        // Registry is initialized via @PostConstruct
    }
    
    @Test
    void testInitialization_ShouldLoadAllFlows() {
        // Assert that all 6 flows are loaded
        Map<String, FlowSpec> allFlows = flowRegistry.getAllFlows();
        assertNotNull(allFlows);
        assertEquals(6, allFlows.size(), "Should load all 6 flows");
    }
    
    @Test
    void testGetFlowSpec_B2B_ShouldReturnValidSpec() {
        // Act
        FlowSpec flowSpec = flowRegistry.getFlowSpec("B2B");
        
        // Assert
        assertNotNull(flowSpec);
        assertEquals("B2B", flowSpec.getQueueName());
        assertEquals("B2B Integration Flow", flowSpec.getDescription());
        assertTrue(flowSpec.isSchemaValidationEnabled());
        assertTrue(flowSpec.isTransformationEnabled());
        assertEquals("/schemas/b2b-schema.xsd", flowSpec.getSchemaPath());
        assertEquals("/xquery/b2b-transform.xq", flowSpec.getTransformationPath());
        assertEquals("http://adapter-service/b2b/process", flowSpec.getAdapterEndpoint());
        
        // Verify required headers
        assertNotNull(flowSpec.getRequiredHeaders());
        assertTrue(flowSpec.getRequiredHeaders().contains("x-queue-name"));
        assertTrue(flowSpec.getRequiredHeaders().contains("x-correlation-id"));
        assertTrue(flowSpec.getRequiredHeaders().contains("x-message-type"));
    }
    
    @Test
    void testGetFlowSpec_Siebel_ShouldReturnValidSpec() {
        // Act
        FlowSpec flowSpec = flowRegistry.getFlowSpec("Siebel");
        
        // Assert
        assertNotNull(flowSpec);
        assertEquals("Siebel", flowSpec.getQueueName());
        assertEquals("Siebel CRM Integration Flow", flowSpec.getDescription());
        assertTrue(flowSpec.isSchemaValidationEnabled());
        assertTrue(flowSpec.isTransformationEnabled());
        
        // Verify Siebel-specific headers
        assertTrue(flowSpec.getRequiredHeaders().contains("x-session-id"));
    }
    
    @Test
    void testGetFlowSpec_Portal_ShouldReturnValidSpec() {
        // Act
        FlowSpec flowSpec = flowRegistry.getFlowSpec("Portal");
        
        // Assert
        assertNotNull(flowSpec);
        assertEquals("Portal", flowSpec.getQueueName());
        assertEquals("Portal Integration Flow", flowSpec.getDescription());
        assertFalse(flowSpec.isSchemaValidationEnabled(), "Portal should have validation disabled");
        assertTrue(flowSpec.isTransformationEnabled());
        
        // Verify Portal-specific headers
        assertTrue(flowSpec.getRequiredHeaders().contains("x-user-id"));
    }
    
    @Test
    void testGetFlowSpec_GenericFlow_ShouldReturnValidSpec() {
        // Act
        FlowSpec flowSpec = flowRegistry.getFlowSpec("Flow");
        
        // Assert
        assertNotNull(flowSpec);
        assertEquals("Flow", flowSpec.getQueueName());
        assertEquals("Generic Flow Integration", flowSpec.getDescription());
        assertTrue(flowSpec.isSchemaValidationEnabled());
        assertFalse(flowSpec.isTransformationEnabled(), "Generic Flow should have transformation disabled");
    }
    
    @Test
    void testGetFlowSpec_NEO_ShouldReturnValidSpec() {
        // Act
        FlowSpec flowSpec = flowRegistry.getFlowSpec("NEO");
        
        // Assert
        assertNotNull(flowSpec);
        assertEquals("NEO", flowSpec.getQueueName());
        assertEquals("NEO Platform Integration Flow", flowSpec.getDescription());
        assertTrue(flowSpec.isSchemaValidationEnabled());
        assertTrue(flowSpec.isTransformationEnabled());
        
        // Verify NEO-specific headers
        assertTrue(flowSpec.getRequiredHeaders().contains("x-platform-id"));
    }
    
    @Test
    void testGetFlowSpec_Sync_ShouldReturnValidSpec() {
        // Act
        FlowSpec flowSpec = flowRegistry.getFlowSpec("Sync");
        
        // Assert
        assertNotNull(flowSpec);
        assertEquals("Sync", flowSpec.getQueueName());
        assertEquals("Synchronous Integration Flow", flowSpec.getDescription());
        assertFalse(flowSpec.isSchemaValidationEnabled(), "Sync should have validation disabled");
        assertFalse(flowSpec.isTransformationEnabled(), "Sync should have transformation disabled");
        
        // Verify Sync-specific headers
        assertTrue(flowSpec.getRequiredHeaders().contains("x-sync-mode"));
    }
    
    @Test
    void testGetFlowSpec_UnknownQueue_ShouldReturnNull() {
        // Act
        FlowSpec flowSpec = flowRegistry.getFlowSpec("UnknownQueue");
        
        // Assert
        assertNull(flowSpec);
    }
    
    @Test
    void testHasFlow_ExistingFlow_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(flowRegistry.hasFlow("B2B"));
        assertTrue(flowRegistry.hasFlow("Siebel"));
        assertTrue(flowRegistry.hasFlow("Portal"));
        assertTrue(flowRegistry.hasFlow("Flow"));
        assertTrue(flowRegistry.hasFlow("NEO"));
        assertTrue(flowRegistry.hasFlow("Sync"));
    }
    
    @Test
    void testHasFlow_NonExistingFlow_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(flowRegistry.hasFlow("UnknownQueue"));
        assertFalse(flowRegistry.hasFlow(""));
        assertFalse(flowRegistry.hasFlow(null));
    }
    
    @Test
    void testGetAllFlows_ShouldContainAllFlows() {
        // Act
        Map<String, FlowSpec> allFlows = flowRegistry.getAllFlows();
        
        // Assert
        assertNotNull(allFlows);
        assertEquals(6, allFlows.size());
        assertTrue(allFlows.containsKey("B2B"));
        assertTrue(allFlows.containsKey("Siebel"));
        assertTrue(allFlows.containsKey("Portal"));
        assertTrue(allFlows.containsKey("Flow"));
        assertTrue(allFlows.containsKey("NEO"));
        assertTrue(allFlows.containsKey("Sync"));
    }
    
    @Test
    void testFlowConfigurations_ShouldMatchYAML() {
        // Test that each flow has the expected configuration as per YAML
        
        // B2B: validation=true, transformation=true
        FlowSpec b2b = flowRegistry.getFlowSpec("B2B");
        assertTrue(b2b.isSchemaValidationEnabled() && b2b.isTransformationEnabled());
        
        // Siebel: validation=true, transformation=true
        FlowSpec siebel = flowRegistry.getFlowSpec("Siebel");
        assertTrue(siebel.isSchemaValidationEnabled() && siebel.isTransformationEnabled());
        
        // Portal: validation=false, transformation=true
        FlowSpec portal = flowRegistry.getFlowSpec("Portal");
        assertFalse(portal.isSchemaValidationEnabled());
        assertTrue(portal.isTransformationEnabled());
        
        // Flow: validation=true, transformation=false
        FlowSpec flow = flowRegistry.getFlowSpec("Flow");
        assertTrue(flow.isSchemaValidationEnabled());
        assertFalse(flow.isTransformationEnabled());
        
        // NEO: validation=true, transformation=true
        FlowSpec neo = flowRegistry.getFlowSpec("NEO");
        assertTrue(neo.isSchemaValidationEnabled() && neo.isTransformationEnabled());
        
        // Sync: validation=false, transformation=false
        FlowSpec sync = flowRegistry.getFlowSpec("Sync");
        assertFalse(sync.isSchemaValidationEnabled());
        assertFalse(sync.isTransformationEnabled());
    }
}
