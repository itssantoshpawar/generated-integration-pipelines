package com.osb.integration.config;

import com.osb.integration.model.FlowSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlowConfigurationTest {

    private FlowConfiguration flowConfiguration;

    @BeforeEach
    void setUp() {
        flowConfiguration = new FlowConfiguration();
        
        Map<String, FlowSpec> specs = new HashMap<>();
        
        FlowSpec b2bFlow = new FlowSpec("B2B", "Q.B2B.IN", Arrays.asList("msg-id", "correlation-id"));
        b2bFlow.setXqueryTransformation("/xquery/b2b.xq");
        specs.put("B2B", b2bFlow);
        
        FlowSpec siebelFlow = new FlowSpec("Siebel", "Q.SIEBEL.IN", Arrays.asList("msg-id", "correlation-id"));
        siebelFlow.setXqueryTransformation("/xquery/siebel.xq");
        specs.put("Siebel", siebelFlow);
        
        flowConfiguration.setSpecs(specs);
    }

    @Test
    void testGetFlowSpecByQueueName() {
        FlowSpec flowSpec = flowConfiguration.getFlowSpecByQueueName("Q.B2B.IN");
        
        assertNotNull(flowSpec);
        assertEquals("B2B", flowSpec.getFlowName());
        assertEquals("Q.B2B.IN", flowSpec.getQueueName());
    }

    @Test
    void testGetFlowSpecByQueueNameNotFound() {
        FlowSpec flowSpec = flowConfiguration.getFlowSpecByQueueName("Q.UNKNOWN.IN");
        
        assertNull(flowSpec);
    }

    @Test
    void testGetFlowSpecByFlowName() {
        FlowSpec flowSpec = flowConfiguration.getFlowSpecByFlowName("Siebel");
        
        assertNotNull(flowSpec);
        assertEquals("Siebel", flowSpec.getFlowName());
        assertEquals("Q.SIEBEL.IN", flowSpec.getQueueName());
    }

    @Test
    void testGetFlowSpecByFlowNameNotFound() {
        FlowSpec flowSpec = flowConfiguration.getFlowSpecByFlowName("Unknown");
        
        assertNull(flowSpec);
    }

    @Test
    void testGetFlows() {
        Map<String, FlowSpec> specs = flowConfiguration.getSpecs();
        
        assertNotNull(specs);
        assertEquals(2, specs.size());
        assertTrue(specs.containsKey("B2B"));
        assertTrue(specs.containsKey("Siebel"));
    }
}
