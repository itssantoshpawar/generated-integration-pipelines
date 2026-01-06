package com.osb.integration.service;

import com.osb.integration.helper.FlowProcessingException;
import com.osb.integration.helper.McpGenericHelper;
import com.osb.integration.model.FlowSpec;
import com.osb.integration.registry.FlowRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Mcpt2rService
 */
@ExtendWith(MockitoExtension.class)
class Mcpt2rServiceTest {
    
    @Mock
    private FlowRegistry flowRegistry;
    
    @Mock
    private McpGenericHelper genericHelper;
    
    @InjectMocks
    private Mcpt2rService service;
    
    private Map<String, String> headers;
    private String payload;
    
    @BeforeEach
    void setUp() {
        headers = new HashMap<>();
        payload = "<message>test</message>";
    }
    
    @Test
    void testProcessMessage_B2BFlow_ShouldDelegateToHelper() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "corr-123");
        
        FlowSpec flowSpec = createFlowSpec("B2B");
        when(flowRegistry.getFlowSpec("B2B")).thenReturn(flowSpec);
        when(genericHelper.processFlow(eq(flowSpec), eq(headers), eq(payload)))
            .thenReturn("<response>success</response>");
        
        // Act
        String result = service.processMessage(headers, payload);
        
        // Assert
        assertNotNull(result);
        assertEquals("<response>success</response>", result);
        verify(flowRegistry, times(1)).getFlowSpec("B2B");
        verify(genericHelper, times(1)).processFlow(eq(flowSpec), eq(headers), eq(payload));
    }
    
    @Test
    void testProcessMessage_SiebelFlow_ShouldDelegateToHelper() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "Siebel");
        headers.put("x-correlation-id", "corr-456");
        
        FlowSpec flowSpec = createFlowSpec("Siebel");
        when(flowRegistry.getFlowSpec("Siebel")).thenReturn(flowSpec);
        when(genericHelper.processFlow(eq(flowSpec), eq(headers), eq(payload)))
            .thenReturn("<response>success</response>");
        
        // Act
        String result = service.processMessage(headers, payload);
        
        // Assert
        assertNotNull(result);
        verify(flowRegistry, times(1)).getFlowSpec("Siebel");
        verify(genericHelper, times(1)).processFlow(any(), eq(headers), eq(payload));
    }
    
    @Test
    void testProcessMessage_PortalFlow_ShouldDelegateToHelper() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "Portal");
        headers.put("x-correlation-id", "corr-789");
        
        FlowSpec flowSpec = createFlowSpec("Portal");
        when(flowRegistry.getFlowSpec("Portal")).thenReturn(flowSpec);
        when(genericHelper.processFlow(eq(flowSpec), eq(headers), eq(payload)))
            .thenReturn("<response>success</response>");
        
        // Act
        String result = service.processMessage(headers, payload);
        
        // Assert
        assertNotNull(result);
        verify(flowRegistry, times(1)).getFlowSpec("Portal");
        verify(genericHelper, times(1)).processFlow(any(), eq(headers), eq(payload));
    }
    
    @Test
    void testProcessMessage_GenericFlow_ShouldDelegateToHelper() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "Flow");
        headers.put("x-correlation-id", "corr-111");
        
        FlowSpec flowSpec = createFlowSpec("Flow");
        when(flowRegistry.getFlowSpec("Flow")).thenReturn(flowSpec);
        when(genericHelper.processFlow(eq(flowSpec), eq(headers), eq(payload)))
            .thenReturn("<response>success</response>");
        
        // Act
        String result = service.processMessage(headers, payload);
        
        // Assert
        assertNotNull(result);
        verify(flowRegistry, times(1)).getFlowSpec("Flow");
        verify(genericHelper, times(1)).processFlow(any(), eq(headers), eq(payload));
    }
    
    @Test
    void testProcessMessage_NEOFlow_ShouldDelegateToHelper() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "NEO");
        headers.put("x-correlation-id", "corr-222");
        
        FlowSpec flowSpec = createFlowSpec("NEO");
        when(flowRegistry.getFlowSpec("NEO")).thenReturn(flowSpec);
        when(genericHelper.processFlow(eq(flowSpec), eq(headers), eq(payload)))
            .thenReturn("<response>success</response>");
        
        // Act
        String result = service.processMessage(headers, payload);
        
        // Assert
        assertNotNull(result);
        verify(flowRegistry, times(1)).getFlowSpec("NEO");
        verify(genericHelper, times(1)).processFlow(any(), eq(headers), eq(payload));
    }
    
    @Test
    void testProcessMessage_SyncFlow_ShouldDelegateToHelper() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "Sync");
        headers.put("x-correlation-id", "corr-333");
        
        FlowSpec flowSpec = createFlowSpec("Sync");
        when(flowRegistry.getFlowSpec("Sync")).thenReturn(flowSpec);
        when(genericHelper.processFlow(eq(flowSpec), eq(headers), eq(payload)))
            .thenReturn("<response>success</response>");
        
        // Act
        String result = service.processMessage(headers, payload);
        
        // Assert
        assertNotNull(result);
        verify(flowRegistry, times(1)).getFlowSpec("Sync");
        verify(genericHelper, times(1)).processFlow(any(), eq(headers), eq(payload));
    }
    
    @Test
    void testProcessMessage_MissingQueueName_ShouldThrowException() throws FlowProcessingException {
        // Arrange - no x-queue-name header
        headers.put("x-correlation-id", "corr-123");
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class,
            () -> service.processMessage(headers, payload));
        assertTrue(exception.getMessage().contains("x-queue-name"));
        verify(flowRegistry, never()).getFlowSpec(any());
        verify(genericHelper, never()).processFlow(any(), any(), any());
    }
    
    @Test
    void testProcessMessage_EmptyQueueName_ShouldThrowException() {
        // Arrange
        headers.put("x-queue-name", "");
        headers.put("x-correlation-id", "corr-123");
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class,
            () -> service.processMessage(headers, payload));
        assertTrue(exception.getMessage().contains("x-queue-name"));
        verify(flowRegistry, never()).getFlowSpec(any());
    }
    
    @Test
    void testProcessMessage_UnknownQueueName_ShouldThrowException() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "UnknownQueue");
        headers.put("x-correlation-id", "corr-123");
        when(flowRegistry.getFlowSpec("UnknownQueue")).thenReturn(null);
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class,
            () -> service.processMessage(headers, payload));
        assertTrue(exception.getMessage().contains("Unknown queue name"));
        verify(flowRegistry, times(1)).getFlowSpec("UnknownQueue");
        verify(genericHelper, never()).processFlow(any(), any(), any());
    }
    
    @Test
    void testProcessMessage_HelperThrowsException_ShouldPropagate() throws FlowProcessingException {
        // Arrange
        headers.put("x-queue-name", "B2B");
        headers.put("x-correlation-id", "corr-123");
        
        FlowSpec flowSpec = createFlowSpec("B2B");
        when(flowRegistry.getFlowSpec("B2B")).thenReturn(flowSpec);
        when(genericHelper.processFlow(any(), any(), any()))
            .thenThrow(new FlowProcessingException("Helper error"));
        
        // Act & Assert
        FlowProcessingException exception = assertThrows(FlowProcessingException.class,
            () -> service.processMessage(headers, payload));
        assertTrue(exception.getMessage().contains("Helper error"));
        verify(flowRegistry, times(1)).getFlowSpec("B2B");
        verify(genericHelper, times(1)).processFlow(any(), any(), any());
    }
    
    private FlowSpec createFlowSpec(String queueName) {
        return FlowSpec.builder()
            .queueName(queueName)
            .description(queueName + " Flow")
            .requiredHeaders(Arrays.asList("x-queue-name", "x-correlation-id"))
            .schemaValidationEnabled(true)
            .schemaPath("/schemas/" + queueName.toLowerCase() + "-schema.xsd")
            .transformationEnabled(true)
            .transformationPath("/xquery/" + queueName.toLowerCase() + "-transform.xq")
            .adapterEndpoint("http://adapter-service/" + queueName.toLowerCase() + "/process")
            .properties("flow=" + queueName)
            .build();
    }
}
