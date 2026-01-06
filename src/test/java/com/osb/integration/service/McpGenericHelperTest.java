package com.osb.integration.service;

import com.osb.integration.config.FlowConfiguration;
import com.osb.integration.model.FlowSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class McpGenericHelperTest {

    @Mock
    private FlowConfiguration flowConfiguration;

    private McpGenericHelper mcpGenericHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mcpGenericHelper = new McpGenericHelper(flowConfiguration);
    }

    @Test
    void testValidateHeadersSuccess() {
        Map<String, String> headers = new HashMap<>();
        headers.put("msg-id", "123");
        headers.put("correlation-id", "abc");
        
        List<String> requiredHeaders = Arrays.asList("msg-id", "correlation-id");
        
        assertDoesNotThrow(() -> mcpGenericHelper.validateHeaders(headers, requiredHeaders));
    }

    @Test
    void testValidateHeadersMissingHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("msg-id", "123");
        
        List<String> requiredHeaders = Arrays.asList("msg-id", "correlation-id");
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mcpGenericHelper.validateHeaders(headers, requiredHeaders)
        );
        
        assertTrue(exception.getMessage().contains("Missing required header: correlation-id"));
    }

    @Test
    void testValidateHeadersNullValue() {
        Map<String, String> headers = new HashMap<>();
        headers.put("msg-id", "123");
        headers.put("correlation-id", null);
        
        List<String> requiredHeaders = Arrays.asList("msg-id", "correlation-id");
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mcpGenericHelper.validateHeaders(headers, requiredHeaders)
        );
        
        assertTrue(exception.getMessage().contains("Missing required header: correlation-id"));
    }

    @Test
    void testValidateHeadersEmptyRequiredList() {
        Map<String, String> headers = new HashMap<>();
        headers.put("msg-id", "123");
        
        List<String> requiredHeaders = Arrays.asList();
        
        assertDoesNotThrow(() -> mcpGenericHelper.validateHeaders(headers, requiredHeaders));
    }

    @Test
    void testValidateHeadersNullRequiredList() {
        Map<String, String> headers = new HashMap<>();
        headers.put("msg-id", "123");
        
        assertDoesNotThrow(() -> mcpGenericHelper.validateHeaders(headers, null));
    }

    @Test
    void testApplyTransformationNoTransformation() {
        String payload = "<xml>test</xml>";
        
        String result = mcpGenericHelper.applyTransformation(payload, null);
        
        assertEquals(payload, result);
    }

    @Test
    void testApplyTransformationEmptyPath() {
        String payload = "<xml>test</xml>";
        
        String result = mcpGenericHelper.applyTransformation(payload, "");
        
        assertEquals(payload, result);
    }

    @Test
    void testApplyTransformationFileNotFound() {
        String payload = "<xml>test</xml>";
        
        String result = mcpGenericHelper.applyTransformation(payload, "/non-existent.xq");
        
        assertEquals(payload, result);
    }

    @Test
    void testRouteToAdapterNoEndpoint() {
        String payload = "<xml>test</xml>";
        Map<String, String> headers = new HashMap<>();
        
        String result = mcpGenericHelper.routeToAdapter(payload, null, headers);
        
        assertEquals(payload, result);
    }

    @Test
    void testRouteToAdapterEmptyEndpoint() {
        String payload = "<xml>test</xml>";
        Map<String, String> headers = new HashMap<>();
        
        String result = mcpGenericHelper.routeToAdapter(payload, "", headers);
        
        assertEquals(payload, result);
    }

    @Test
    void testRouteToAdapterWithEndpoint() {
        String payload = "<xml>test</xml>";
        Map<String, String> headers = new HashMap<>();
        String endpoint = "http://adapter-service/test";
        
        String result = mcpGenericHelper.routeToAdapter(payload, endpoint, headers);
        
        assertTrue(result.contains("SUCCESS"));
        assertTrue(result.contains(endpoint));
    }

    @Test
    void testProcessMessageSuccess() {
        String queueName = "Q.B2B.IN";
        Map<String, String> headers = new HashMap<>();
        headers.put("msg-id", "123");
        headers.put("correlation-id", "abc");
        headers.put("source-system", "TEST");
        String payload = "<xml>test</xml>";
        
        FlowSpec flowSpec = new FlowSpec("B2B", queueName, Arrays.asList("msg-id", "correlation-id", "source-system"));
        flowSpec.setXqueryTransformation("/xquery/b2b.xq");
        flowSpec.setAdapterEndpoint("http://adapter-service/b2b");
        flowSpec.setSchemaValidationEnabled(false);
        
        when(flowConfiguration.getFlowSpecByQueueName(queueName)).thenReturn(flowSpec);
        
        String result = mcpGenericHelper.processMessage(queueName, headers, payload);
        
        assertNotNull(result);
        assertTrue(result.contains("SUCCESS"));
        verify(flowConfiguration).getFlowSpecByQueueName(queueName);
    }

    @Test
    void testProcessMessageNoFlowSpec() {
        String queueName = "Q.UNKNOWN.IN";
        Map<String, String> headers = new HashMap<>();
        String payload = "<xml>test</xml>";
        
        when(flowConfiguration.getFlowSpecByQueueName(queueName)).thenReturn(null);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mcpGenericHelper.processMessage(queueName, headers, payload)
        );
        
        assertTrue(exception.getMessage().contains("No flow configuration found"));
    }

    @Test
    void testProcessMessageMissingHeaders() {
        String queueName = "Q.B2B.IN";
        Map<String, String> headers = new HashMap<>();
        headers.put("msg-id", "123");
        String payload = "<xml>test</xml>";
        
        FlowSpec flowSpec = new FlowSpec("B2B", queueName, Arrays.asList("msg-id", "correlation-id"));
        
        when(flowConfiguration.getFlowSpecByQueueName(queueName)).thenReturn(flowSpec);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mcpGenericHelper.processMessage(queueName, headers, payload)
        );
        
        assertTrue(exception.getMessage().contains("Missing required header"));
    }

    @Test
    void testGetFlowSpec() {
        String queueName = "Q.B2B.IN";
        FlowSpec flowSpec = new FlowSpec("B2B", queueName, Arrays.asList("msg-id"));
        
        when(flowConfiguration.getFlowSpecByQueueName(queueName)).thenReturn(flowSpec);
        
        FlowSpec result = mcpGenericHelper.getFlowSpec(queueName);
        
        assertNotNull(result);
        assertEquals("B2B", result.getFlowName());
        verify(flowConfiguration).getFlowSpecByQueueName(queueName);
    }
}
