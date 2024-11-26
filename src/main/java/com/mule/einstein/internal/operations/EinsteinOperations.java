package com.mule.einstein.internal.operations;

import com.mule.einstein.internal.connection.EinsteinConnection;
import com.mule.einstein.internal.error.provider.ChatErrorTypeProvider;
import com.mule.einstein.internal.error.provider.EmbeddingErrorTypeProvider;
import com.mule.einstein.internal.helpers.PayloadHelper;
import com.mule.einstein.internal.helpers.PromptTemplateHelper;
import com.mule.einstein.internal.helpers.ResponseHelper;
import com.mule.einstein.internal.helpers.chatmemory.ChatMemoryHelper;
import com.mule.einstein.internal.helpers.documents.ParametersEmbeddingDocument;
import com.mule.einstein.internal.models.ParamsModelDetails;
import com.mule.einstein.internal.models.RAGParamsModelDetails;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.mule.einstein.internal.error.EinsteinErrorType.*;
import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_ADA_002;
import static java.lang.String.format;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class EinsteinOperations {

  private static final Logger log = LoggerFactory.getLogger(EinsteinOperations.class);

  /**
   * Generate a response based on a list of messages representing a chat conversation.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-generate-from-messages")
  @Throws(ChatErrorTypeProvider.class)
  public Result<InputStream, Void> generateChat(String messages, @Connection EinsteinConnection connection,
                                                @ParameterGroup(name = "Additional properties") ParamsModelDetails paramDetails) {

    log.info("Executing chat generate from message operation.");
    try {
      String response = PayloadHelper.executeGenerateChat(messages, connection, paramDetails);
      return ResponseHelper.createEinsteinResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing chat generate from message operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating the chat from messages " + messages, CHAT_FAILURE, e);
    }
  }

  /**
   * Generate a response based on a file embedding.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-adhoc-file-query")
  @Throws(EmbeddingErrorTypeProvider.class)
  public Result<InputStream, Void> queryEmbeddingOnFiles(String prompt, String filePath,
                                                         @Connection EinsteinConnection connection,
                                                         @ParameterGroup(
                                                             name = "Additional properties") ParametersEmbeddingDocument paramDetails) {

    log.info("Executing embedding adhoc file query operation.");
    try {
      String response = PayloadHelper.embeddingFileQuery(prompt, filePath, connection, paramDetails.getModelName(),
                                                         paramDetails.getFileType(), paramDetails.getOptionType());
      return ResponseHelper.createEinsteinResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing embedding adhoc file query operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating the chat from filePath " + filePath + ", for prompt " + prompt,
                                EMBEDDING_OPERATIONS_FAILURE, e);
    }
  }

  /**
   * Generate a response based on a plain text prompt and file from embedding and LLM.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("RAG-adhoc-load-document")
  @Throws(EmbeddingErrorTypeProvider.class)
  public Result<InputStream, Void> ragOnFiles(String prompt, String filePath, @Connection EinsteinConnection connection,
                                              @ParameterGroup(name = "Additional properties") RAGParamsModelDetails paramDetails) {

    log.info("Executing rag adhoc load document.");
    try {
      String content = PayloadHelper.embeddingFileQuery(prompt, filePath, connection, paramDetails.getEmbeddingName(),
                                                        paramDetails.getFileType(), paramDetails.getOptionType());
      String response = PayloadHelper.executeRAG("data: " + content + ", question: " + prompt, connection,
                                                 paramDetails);
      return ResponseHelper.createEinsteinResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing rag adhoc load document operation %s", e.getMessage()), e);
      throw new ModuleException("Error while doing rag adhoc load document from filePath " + filePath + ", for prompt "
          + prompt, RAG_FAILURE, e);
    }
  }

  /**
   * Generate a response based on a plain text prompt and tools config.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Tools-use-ai-service")
  @Throws(EmbeddingErrorTypeProvider.class)
  public Result<InputStream, Void> executeTools(String prompt, String toolsConfig, @Connection EinsteinConnection connection,
                                                @ParameterGroup(name = "Additional properties") ParamsModelDetails paramDetails) {

    log.info("Executing AI service tools operation.");
    try {
      String content =
          PayloadHelper.embeddingFileQuery(prompt, toolsConfig, connection, OPENAI_ADA_002, "text", "FULL");
      String response = PayloadHelper.executeTools(prompt, "data: " + content + ", question: " + prompt,
                                                   toolsConfig, connection, paramDetails);
      return ResponseHelper.createEinsteinResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing AI service tools operation %s", e.getMessage()), e);
      throw new ModuleException("Error while executing einstein tools with provided config " + toolsConfig + ", for prompt "
          + prompt, TOOLS_OPERATION_FAILURE, e);
    }
  }

  /**
   * Generate a response based on the prompt provided.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt")
  @Throws(ChatErrorTypeProvider.class)
  public Result<InputStream, Void> generateText(String prompt, @Connection EinsteinConnection connection,
                                                @ParameterGroup(name = "Additional properties") ParamsModelDetails paramDetails) {

    log.info("Executing chat answer prompt operation.");
    try {
      String response = PayloadHelper.executeGenerateText(prompt, connection, paramDetails);
      return ResponseHelper.createEinsteinResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing chat answer prompt operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating text for prompt " + prompt, CHAT_FAILURE, e);
    }
  }

  /**
   * Generate a response based on the prompt using chat memory.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt-with-memory")
  @Throws(ChatErrorTypeProvider.class)
  public Result<InputStream, Void> generateTextMemeory(String prompt, String memoryPath, String memoryName,
                                                       Integer keepLastMessages, @Connection EinsteinConnection connection,
                                                       @ParameterGroup(
                                                           name = "Additional properties") ParamsModelDetails paramDetails) {

    log.info("Executing chat answer prompt with memory operation.");
    try {
      String response = ChatMemoryHelper.chatWithMemory(prompt, memoryPath, memoryName, keepLastMessages,
                                                        connection, paramDetails);
      return ResponseHelper.createEinsteinResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing chat answer prompt with memory operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating text from memory path " + memoryPath + ", memory name "
          + memoryName + ", for prompt " + prompt, CHAT_FAILURE, e);
    }
  }

  /**
   * Helps defining an AI Agent with a prompt template
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("AGENT-define-prompt-template")
  @Throws(ChatErrorTypeProvider.class)
  public Result<InputStream, Void> definePromptTemplate(String template, String instructions, String dataset,
                                                        @Connection EinsteinConnection connection, @ParameterGroup(
                                                            name = "Additional properties") ParamsModelDetails paramDetails) {

    log.info("Executing agent defined prompt template operation.");
    try {
      String finalPromptTemplate = PromptTemplateHelper.definePromptTemplate(template, instructions, dataset);
      String response = PayloadHelper.executeGenerateText(finalPromptTemplate, connection, paramDetails);
      return ResponseHelper.createEinsteinResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing agent defined prompt template operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating prompt from template " + template + ", instructions "
          + instructions + ", dataset " + dataset, CHAT_FAILURE, e);
    }
  }
}
