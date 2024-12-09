package com.mule.einstein.internal.operations;

import com.mule.einstein.internal.connection.EinsteinConnection;
import com.mule.einstein.internal.error.EinsteinErrorType;
import com.mule.einstein.internal.helpers.PayloadHelper;
import com.mule.einstein.internal.helpers.chatmemory.ChatMemoryHelper;
import com.mule.einstein.internal.models.ParamsModelDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mule.runtime.extension.api.exception.ModuleException;

import static com.mule.einstein.internal.error.EinsteinErrorType.CHAT_FAILURE;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class EinsteinGenerationOperationsTest {

  private EinsteinGenerationOperations einsteinGenerationOperationsTest;

  @BeforeEach
  public void setUp() {
    einsteinGenerationOperationsTest = new EinsteinGenerationOperations();
  }

  @Test
  public void testDefinePromptTemplateFailure() {
    String template = "Template";
    String instructions = "Instructions";
    String dataset = "Dataset";
    // Mock connection
    EinsteinConnection connection = mock(EinsteinConnection.class);
    // Mock parameter group
    ParamsModelDetails paramDetails = mock(ParamsModelDetails.class);

    try (MockedStatic<PayloadHelper> mockedPayloadHelper = Mockito.mockStatic(PayloadHelper.class)) {
      mockedPayloadHelper.when(() -> PayloadHelper.executeGenerateText(anyString(), eq(connection), eq(paramDetails)))
          .thenThrow(new RuntimeException("Simulated Error"));

      ModuleException exception = assertThrows(ModuleException.class, () -> {
        einsteinGenerationOperationsTest.definePromptTemplate(
                                                              template, instructions, dataset, connection, paramDetails);
      });
      assertEquals("Error while generating prompt from template Template, instructions Instructions, dataset Dataset",
                   exception.getMessage());
      assertEquals(EinsteinErrorType.CHAT_FAILURE, exception.getType());

      mockedPayloadHelper.verify(() -> PayloadHelper.executeGenerateText(anyString(), eq(connection), eq(paramDetails)),
                                 times(1));
    }
  }

  @Test
  public void testGenerateTextFailure() {
    String prompt = "Test Prompt";
    EinsteinConnection connection = mock(EinsteinConnection.class); // Mock the connection
    ParamsModelDetails paramDetails = mock(ParamsModelDetails.class); // Mock parameter details

    // Mock the static PayloadHelper to throw an exception
    try (MockedStatic<PayloadHelper> mockedPayloadHelper = Mockito.mockStatic(PayloadHelper.class)) {
      mockedPayloadHelper
          .when(() -> PayloadHelper.executeGenerateText(eq(prompt), eq(connection), eq(paramDetails)))
          .thenThrow(new RuntimeException("Simulated Exception"));

      // Act & Assert
      ModuleException exception = assertThrows(ModuleException.class, () -> {
        einsteinGenerationOperationsTest.generateText(prompt, connection, paramDetails);
      });

      // Assertions
      assertEquals(
                   "Error while generating text for prompt Test Prompt",
                   exception.getMessage());
      assertEquals(CHAT_FAILURE, exception.getType());

      mockedPayloadHelper.verify(
                                 () -> PayloadHelper.executeGenerateText(eq(prompt), eq(connection), eq(paramDetails)),
                                 times(1));
    }
  }

  @Test
  public void testGenerateTextMemoryFailure() {
    String prompt = "Test";
    String memoryPath = "src/resources/testdb";
    String memoryName = "vt";
    Integer keepLastMessages = 10;
    // Mock connection
    EinsteinConnection connection = mock(EinsteinConnection.class);
    // Mock parameter group
    ParamsModelDetails paramDetails = mock(ParamsModelDetails.class);

    try (MockedStatic<ChatMemoryHelper> mockedChatMemoryHelper = Mockito.mockStatic(ChatMemoryHelper.class)) {
      mockedChatMemoryHelper
          .when(() -> ChatMemoryHelper.chatWithMemory(
                                                      eq(prompt), eq(memoryPath), eq(memoryName), eq(keepLastMessages),
                                                      eq(connection), eq(paramDetails)))
          .thenThrow(new RuntimeException("Simulated Chat Memory Error"));

      // Act & Assert
      ModuleException exception = assertThrows(ModuleException.class, () -> {
        einsteinGenerationOperationsTest.generateTextMemory(
                                                            prompt, memoryPath, memoryName, keepLastMessages, connection,
                                                            paramDetails);
      });

      System.out.println(exception);
      assertEquals(
                   "Error while generating text from memory path src/resources/testdb, memory name vt, for prompt Test",
                   exception.getMessage());
      // Uncomment below if CHAT_FAILURE is part of the exception details.
      assertEquals(CHAT_FAILURE, exception.getType());

      mockedChatMemoryHelper.verify(() -> ChatMemoryHelper.chatWithMemory(
                                                                          eq(prompt), eq(memoryPath), eq(memoryName),
                                                                          eq(keepLastMessages), eq(connection), eq(paramDetails)),
                                    times(1));
    }
  }

  @Test
  public void testGenerateChatFailure() {
    String messages = "Test Messages";
    EinsteinConnection connection = mock(EinsteinConnection.class); // Mock connection
    ParamsModelDetails paramDetails = mock(ParamsModelDetails.class); // Mock parameter group

    try (MockedStatic<PayloadHelper> mockedPayloadHelper = Mockito.mockStatic(PayloadHelper.class)) {
      mockedPayloadHelper
          .when(() -> PayloadHelper.executeGenerateChat(eq(messages), eq(connection), eq(paramDetails)))
          .thenThrow(new RuntimeException("Simulated Chat Generation Error"));

      // Act & Assert
      ModuleException exception = assertThrows(ModuleException.class, () -> {
        einsteinGenerationOperationsTest.generateChat(messages, connection, paramDetails);
      });

      System.out.println(exception);
      assertEquals(
                   "Error while generating the chat from messages Test Messages",
                   exception.getMessage());
      // Uncomment below if CHAT_FAILURE is part of the exception details.
      assertEquals(CHAT_FAILURE, exception.getType());

      mockedPayloadHelper.verify(() -> PayloadHelper.executeGenerateChat(eq(messages), eq(connection), eq(paramDetails)),
                                 times(1));
    }
  }
}
