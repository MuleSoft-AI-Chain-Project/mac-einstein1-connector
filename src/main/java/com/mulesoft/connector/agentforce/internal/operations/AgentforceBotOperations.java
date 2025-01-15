package com.mulesoft.connector.agentforce.internal.operations;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentConversationResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.error.provider.BotErrorTypeProvider;
import com.mulesoft.connector.agentforce.internal.botapi.group.BotMessageParameterGroup;
import com.mulesoft.connector.agentforce.internal.botapi.group.BotAgentParameterGroup;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import org.json.JSONObject;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputJsonType;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.io.IOUtils.toInputStream;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;
import static org.mule.runtime.extension.api.annotation.param.MediaType.TEXT_PLAIN;

public class AgentforceBotOperations {

  private static final Logger log = LoggerFactory.getLogger(AgentforceBotOperations.class);

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Start-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/StartAgentConversationResponse.json")
  public Result<InputStream, InvokeAgentResponseAttributes> startAgentConversation(@Connection AgentforceConnection connection,
                                                                                   @ParameterGroup(
                                                                                       name = "Agent") @MetadataKeyId BotAgentParameterGroup parameterGroup)
      throws IOException {

    try {
      AgentConversationResponseDTO responseDTO = connection.getBotRequestHelper().startSession(parameterGroup.getAgent());
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("sessionId", responseDTO.getSessionId());

      return Result.<InputStream, InvokeAgentResponseAttributes>builder()
          .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
          .attributes(responseDTO.getResponseAttributes())
          .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
          .mediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
          .build();
    } catch (Exception e) {
      return null;
    }
  }


  @MediaType(value = TEXT_PLAIN, strict = false)
  @Alias("Continue-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  public Result<InputStream, InvokeAgentResponseAttributes> continueConversation(@Content String sessionId,
                                                                                 @ParameterGroup(
                                                                                     name = "Message") BotMessageParameterGroup messageParameterGroup,
                                                                                 @Connection AgentforceConnection connection)
      throws IOException {

    AgentConversationResponseDTO responseDTO =
        connection.getBotRequestHelper().continueSession(messageParameterGroup.getMessage(),
                                                         sessionId,
                                                         messageParameterGroup.getMessageSequenceNumber(),
                                                         messageParameterGroup.getInReplyToMessageId());
    return Result.<InputStream, InvokeAgentResponseAttributes>builder()
        .output(responseDTO.getTextInputStream())
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
        .mediaType(org.mule.runtime.api.metadata.MediaType.TEXT)
        .build();
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("End-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  public Result<Void, InvokeAgentResponseAttributes> endConversation(@Content String sessionId,
                                                                     @Connection AgentforceConnection connection)
      throws IOException {
    AgentConversationResponseDTO responseDTO = connection.getBotRequestHelper().endSession(sessionId);

    return Result.<Void, InvokeAgentResponseAttributes>builder()
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
        .build();
  }
}
