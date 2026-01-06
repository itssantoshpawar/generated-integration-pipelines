package com.osb.integration.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlowSpecTest {

    @Test
    void testFlowSpecCreation() {
        List<String> headers = Arrays.asList("msg-id", "correlation-id");
        FlowSpec flowSpec = new FlowSpec("B2B", "Q.B2B.IN", headers);
        
        assertEquals("B2B", flowSpec.getFlowName());
        assertEquals("Q.B2B.IN", flowSpec.getQueueName());
        assertEquals(headers, flowSpec.getRequiredHeaders());
    }

    @Test
    void testFlowSpecSettersAndGetters() {
        FlowSpec flowSpec = new FlowSpec();
        
        flowSpec.setFlowName("Siebel");
        flowSpec.setQueueName("Q.SIEBEL.IN");
        flowSpec.setXqueryTransformation("/xquery/siebel.xq");
        flowSpec.setAdapterEndpoint("http://adapter/siebel");
        flowSpec.setSchemaValidationEnabled(true);
        flowSpec.setSchemaPath("/schemas/siebel.xsd");
        
        Map<String, String> additionalProps = new HashMap<>();
        additionalProps.put("timeout", "30000");
        flowSpec.setAdditionalProperties(additionalProps);
        
        assertEquals("Siebel", flowSpec.getFlowName());
        assertEquals("Q.SIEBEL.IN", flowSpec.getQueueName());
        assertEquals("/xquery/siebel.xq", flowSpec.getXqueryTransformation());
        assertEquals("http://adapter/siebel", flowSpec.getAdapterEndpoint());
        assertTrue(flowSpec.isSchemaValidationEnabled());
        assertEquals("/schemas/siebel.xsd", flowSpec.getSchemaPath());
        assertEquals(additionalProps, flowSpec.getAdditionalProperties());
    }

    @Test
    void testFlowSpecToString() {
        List<String> headers = Arrays.asList("msg-id", "correlation-id");
        FlowSpec flowSpec = new FlowSpec("Portal", "Q.PORTAL.IN", headers);
        flowSpec.setXqueryTransformation("/xquery/portal.xq");
        
        String toString = flowSpec.toString();
        
        assertTrue(toString.contains("flowName='Portal'"));
        assertTrue(toString.contains("queueName='Q.PORTAL.IN'"));
        assertTrue(toString.contains("xqueryTransformation='/xquery/portal.xq'"));
    }
}
