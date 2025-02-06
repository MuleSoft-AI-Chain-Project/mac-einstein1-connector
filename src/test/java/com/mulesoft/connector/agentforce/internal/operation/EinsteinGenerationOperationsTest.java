package com.mulesoft.connector.agentforce.internal.operation;

import com.mulesoft.connector.einstein.internal.connection.EinsteinConnection;
import com.mulesoft.connector.einstein.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.einstein.internal.modelsapi.helpers.chatmemory.ChatMemoryHelper;
import com.mulesoft.connector.einstein.internal.modelsapi.models.ParamsModelDetails;
import com.mulesoft.connector.einstein.internal.operation.EinsteinGenerationOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.IOException;

import static com.mulesoft.connector.einstein.internal.error.AgentforceErrorType.CHAT_FAILURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EinsteinGenerationOperationsTest {

  private EinsteinGenerationOperations einsteinGenerationOperations;

  @Mock
  private RequestHelper requestHelperMock;

  @Mock
  private ChatMemoryHelper chatMemoryHelperMock;

  @Mock
  private EinsteinConnection connectionMock;

  @Mock
  private ParamsModelDetails paramDetailsMock;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    einsteinGenerationOperations = new EinsteinGenerationOperations();
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close(); // Close the resource to avoid resource leaks
  }

  @Test
  void testDefinePromptTemplateFailure() throws IOException {
    String template = "Template";
    String instructions = "Instructions";
    String dataset = "Dataset";
    when(connectionMock.getRequestHelper()).thenReturn(requestHelperMock);
    when(requestHelperMock.executeGenerateText(anyString(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    ModuleException exception = assertThrows(ModuleException.class,
                                             () -> einsteinGenerationOperations.definePromptTemplate(connectionMock, template,
                                                                                                     instructions, dataset,
                                                                                                     paramDetailsMock));

    assertEquals("Error while generating prompt from template Template, instructions Instructions, dataset Dataset",
                 exception.getMessage());
    assertEquals(CHAT_FAILURE, exception.getType());
  }

  @Test
  void testGenerateTextFailure() throws IOException {
    String prompt = "Test Prompt";
    when(connectionMock.getRequestHelper()).thenReturn(requestHelperMock);
    when(requestHelperMock.executeGenerateText(anyString(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    ModuleException exception =
        assertThrows(ModuleException.class,
                     () -> einsteinGenerationOperations.generateText(connectionMock, prompt, paramDetailsMock));

    assertEquals(
                 "Error while generating text for prompt Test Prompt",
                 exception.getMessage());
    assertEquals(CHAT_FAILURE, exception.getType());
  }

  @Test
  void testGenerateTextMemoryFailure() throws IOException {
    String prompt = "Test";
    String memoryPath = "src/resources/testdb";
    String memoryName = "vt";
    Integer keepLastMessages = 10;

    when(connectionMock.getChatMemoryHelper()).thenReturn(chatMemoryHelperMock);
    when(chatMemoryHelperMock.chatWithMemory(anyString(), anyString(), anyString(), anyInt(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    ModuleException exception = assertThrows(ModuleException.class, () -> einsteinGenerationOperations
        .generateTextMemory(connectionMock, prompt, memoryPath, memoryName, keepLastMessages, paramDetailsMock));

    assertEquals(
                 "Error while generating text from memory path src/resources/testdb, memory name vt, for prompt Test",
                 exception.getMessage());
    assertEquals(CHAT_FAILURE, exception.getType());
  }

  @Test
  void testGenerateChatFailure() throws IOException {
    String messages = "Test Messages";
    when(connectionMock.getRequestHelper()).thenReturn(requestHelperMock);
    when(requestHelperMock.generateChatFromMessages(anyString(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    ModuleException exception =
        assertThrows(ModuleException.class,
                     () -> einsteinGenerationOperations.generateChatFromMessages(connectionMock, messages, paramDetailsMock));

    assertEquals(
                 "Error while generating the chat from messages Test Messages",
                 exception.getMessage());
    assertEquals(CHAT_FAILURE, exception.getType());
  }
}

