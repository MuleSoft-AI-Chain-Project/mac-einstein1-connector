package com.mulesoft.connector.agentforce.internal.operations;

import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.error.provider.ChatErrorTypeProvider;
import com.mulesoft.connector.agentforce.internal.helpers.BotRequestHelper;
import com.mulesoft.connector.agentforce.internal.models.BotAgentParameterGroup;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

public class AgentforceBotOperations {

  private static final Logger log = LoggerFactory.getLogger(AgentforceBotOperations.class);

  BotRequestHelper requestHelper = new BotRequestHelper();

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Invoke-Agent")
  @Throws(ChatErrorTypeProvider.class)
  public String startConversation(@Connection AgentforceConnection connection,
                                  @ParameterGroup(name = "Agent") @MetadataKeyId BotAgentParameterGroup parameterGroup)
      throws IOException {

    return requestHelper.startSession(parameterGroup.getAgent(), connection);
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Continue-agent-conversation")
  @Throws(ChatErrorTypeProvider.class)
  public String continueConversation(@Content(primary = true) String body,
                                     @Content String sessionId,
                                     @Connection AgentforceConnection connection)
      throws IOException {

    return requestHelper.continueSession(body, sessionId, connection);
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("End-agent-conversation")
  @Throws(ChatErrorTypeProvider.class)
  public String endConversation(@Content String sessionId, @Connection AgentforceConnection connection)
      throws IOException {

    return requestHelper.endSession(sessionId, connection);
  }
}
