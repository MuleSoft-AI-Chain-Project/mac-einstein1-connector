package com.mulesoft.connector.agentforce.internal.operations;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentConversationResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.error.provider.BotErrorTypeProvider;
import com.mulesoft.connector.agentforce.internal.botapi.group.BotMessageParameterGroup;
import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelper;
import com.mulesoft.connector.agentforce.internal.botapi.group.BotAgentParameterGroup;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.IOException;
import java.io.InputStream;

import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

public class AgentforceBotOperations {

  BotRequestHelper requestHelper = new BotRequestHelper();

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Invoke-Agent")
  @Throws(BotErrorTypeProvider.class)
  public Result<InputStream, InvokeAgentResponseAttributes> invokeAgentConversation(@Connection AgentforceConnection connection,
                                                                                    @ParameterGroup(
                                                                                        name = "Agent") @MetadataKeyId BotAgentParameterGroup parameterGroup)
      throws IOException {

    AgentConversationResponseDTO responseDTO = requestHelper.startSession(connection, parameterGroup.getAgent());

    return Result.<InputStream, InvokeAgentResponseAttributes>builder()
        .output(responseDTO.getTextInputStream())
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
        .mediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
        .build();
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Continue-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  public Result<InputStream, InvokeAgentResponseAttributes> continueConversation(@Content String sessionId,
                                                                                 @ParameterGroup(
                                                                                     name = "Message") BotMessageParameterGroup messageParameterGroup,
                                                                                 @Connection AgentforceConnection connection)
      throws IOException {

    AgentConversationResponseDTO responseDTO = requestHelper.continueSession(messageParameterGroup.getMessage(),
                                                                             sessionId,
                                                                             messageParameterGroup.getMessageSequenceNumber(),
                                                                             messageParameterGroup.getInReplyToMessageId(),
                                                                             connection);
    responseDTO.getResponseAttributes().setSessionId(sessionId);

    return Result.<InputStream, InvokeAgentResponseAttributes>builder()
        .output(responseDTO.getTextInputStream())
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
        .mediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
        .build();
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("End-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  public Result<Void, InvokeAgentResponseAttributes> endConversation(@Content String sessionId,
                                                                     @Connection AgentforceConnection connection)
      throws IOException {

    AgentConversationResponseDTO responseDTO = requestHelper.endSession(sessionId, connection);

    return Result.<Void, InvokeAgentResponseAttributes>builder()
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
        .build();
  }
}
