package com.mulesoft.connector.agentforce.internal.operations;

import com.mulesoft.connector.agentforce.api.metadata.AgentforceResponseAttributes;
import com.mulesoft.connector.agentforce.api.metadata.ResponseParameters;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.modelsapi.error.provider.ChatErrorTypeProvider;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.PromptTemplateHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.ResponseHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsModelDetails;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputJsonType;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType.CHAT_FAILURE;
import static java.lang.String.format;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class AgentforceGenerationOperations {

  private static final Logger log = LoggerFactory.getLogger(AgentforceGenerationOperations.class);

  /**
   * Helps defining an AI Agent with a prompt template
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("AGENT-define-prompt-template")
  @Throws(ChatErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/AgentForceOperationResponse.json")
  public Result<InputStream, AgentforceResponseAttributes> definePromptTemplate(@Content(primary = true) String template,
                                                                                @Content String instructions,
                                                                                @Content String dataset,
                                                                                @Connection AgentforceConnection connection,
                                                                                @ParameterGroup(
                                                                                    name = "Additional properties") ParamsModelDetails paramDetails) {
    log.info("Executing agent defined prompt template operation.");
    System.out.println("Executing agent defined prompt template operation.");
    try {
      String finalPromptTemplate = PromptTemplateHelper.definePromptTemplate(template, instructions, dataset);
      String response = connection.getRequestHelper().executeGenerateText(finalPromptTemplate, paramDetails);

      return ResponseHelper.createAgentforceFormattedResponse(response);
    } catch (Exception e) {

      log.error(format("Exception occurred while executing agent defined prompt template operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating prompt from template " + template + ", instructions "
          + instructions + ", dataset " + dataset, CHAT_FAILURE, e);
    }
  }

  /**
   * Generate a response based on the prompt provided.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt")
  @Throws(ChatErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/AgentForceOperationResponse.json")
  public Result<InputStream, AgentforceResponseAttributes> generateText(@Content String prompt,
                                                                        @Connection AgentforceConnection connection,
                                                                        @ParameterGroup(
                                                                            name = "Additional properties") ParamsModelDetails paramDetails) {
    log.info("Executing chat answer prompt operation.");
    try {
      String response = connection.getRequestHelper().executeGenerateText(prompt, paramDetails);

      return ResponseHelper.createAgentforceFormattedResponse(response);
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
  @OutputJsonType(schema = "api/response/AgentForceOperationResponse.json")
  public Result<InputStream, AgentforceResponseAttributes> generateTextMemory(@Content(primary = true) String prompt,
                                                                              String memoryPath,
                                                                              String memoryName,
                                                                              Integer keepLastMessages,
                                                                              @Connection AgentforceConnection connection,
                                                                              @ParameterGroup(
                                                                                  name = "Additional properties") ParamsModelDetails paramDetails) {
    log.info("Executing chat answer prompt with memory operation.");
    try {

      String response = connection.getChatMemoryHelper().chatWithMemory(prompt, memoryPath, memoryName, keepLastMessages,
                                                                        paramDetails);

      return ResponseHelper.createAgentforceFormattedResponse(response);
    } catch (Exception e) {
      log.error(format("Exception occurred while executing chat answer prompt with memory operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating text from memory path " + memoryPath + ", memory name "
          + memoryName + ", for prompt " + prompt, CHAT_FAILURE, e);
    }
  }

  /**
   * Generate a response based on a list of messages representing a chat conversation.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-generate-from-messages")
  @Throws(ChatErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/AgentForceChatFromMessagesResponse.json")
  public Result<InputStream, ResponseParameters> generateChatFromMessages(@Content String messages,
                                                                          @Connection AgentforceConnection connection,
                                                                          @ParameterGroup(
                                                                              name = "Additional properties") ParamsModelDetails paramDetails) {
    log.info("Executing chat generate from message operation.");
    try {

      String response = connection.getRequestHelper().executeGenerateChat(messages, paramDetails);

      return ResponseHelper.createAgentforceChatFromMessagesResponse(response);
    } catch (Exception e) {

      log.error(format("Exception occurred while executing chat generate from message operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating the chat from messages " + messages, CHAT_FAILURE, e);
    }
  }
}
