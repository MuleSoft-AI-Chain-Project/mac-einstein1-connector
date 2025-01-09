package com.mulesoft.connector.agentforce.internal.operations;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentStartSessionResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.error.provider.BotErrorTypeProvider;
import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelper;
import com.mulesoft.connector.agentforce.internal.botapi.models.BotAgentParameterGroup;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

public class AgentforceBotOperations {

  private static final Logger log = LoggerFactory.getLogger(AgentforceBotOperations.class);

  BotRequestHelper requestHelper = new BotRequestHelper();

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Invoke-Agent")
  @Throws(BotErrorTypeProvider.class)
  public Result<InputStream, InvokeAgentResponseAttributes> invokeAgentConversation(@Connection AgentforceConnection connection,
                                                                                    @ParameterGroup(
                                                                                        name = "Agent") @MetadataKeyId BotAgentParameterGroup parameterGroup)
      throws IOException {

    AgentStartSessionResponseDTO responseDTO = requestHelper.startSession(connection, parameterGroup.getAgent());

    return Result.<InputStream, InvokeAgentResponseAttributes>builder()
        .output(responseDTO.getTextInputStream())
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
        .mediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
        .build();
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Continue-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  public String continueConversation(@Content(primary = true) String message,
                                     @Content String sessionId,
                                     @Connection AgentforceConnection connection)
      throws IOException {

    return requestHelper.continueSession(message, sessionId, connection);
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("End-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  public String endConversation(@Content String sessionId, @Connection AgentforceConnection connection)
      throws IOException {

    return requestHelper.endSession(sessionId, connection);
  }
}
