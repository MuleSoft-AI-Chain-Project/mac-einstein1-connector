package com.mulesoft.connector.agentforce.internal.operation;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;
import com.mulesoft.connector.agentforce.internal.error.provider.BotErrorTypeProvider;
import com.mulesoft.connector.agentforce.internal.botapi.group.BotAgentParameterGroup;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.metadata.AgentConversationResponseMetadataResolver;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputJsonType;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType.AGENT_OPERATIONS_FAILURE;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;
import static org.mule.runtime.extension.api.annotation.param.MediaType.TEXT_PLAIN;

public class AgentforceBotOperations {

  private static final Logger log = LoggerFactory.getLogger(AgentforceBotOperations.class);

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Start-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/StartAgentConversationResponse.json")
  public void startAgentConversation(@Connection AgentforceConnection connection,
                                     @Placement(order = 1) @ParameterGroup(
                                         name = "Agent") @MetadataKeyId BotAgentParameterGroup parameterGroup,
                                     CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback) {

    log.info("Executing start agent conversation operation, agent = {}", parameterGroup.getAgent());

    try {
      connection.getBotRequestHelper().startSession(parameterGroup.getAgent(), callback);
    } catch (Exception e) {
      callback.error(new ModuleException("Error while starting agent conversation for agent: " + parameterGroup.getAgent(),
                                         AGENT_OPERATIONS_FAILURE, e));
    }
  }

  @MediaType(value = TEXT_PLAIN, strict = false)
  @Alias("Continue-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  @OutputResolver(output = AgentConversationResponseMetadataResolver.class)
  public void continueConversation(@Connection AgentforceConnection connection,
                                   @Content(primary = true) InputStream message,
                                   @Content String sessionId,
                                   @Summary("Increase this number for each subsequent message in a session") @DisplayName("Message Sequence Number") int messageSequenceNumber,
                                   CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback) {

    log.info("Executing continue agent conversation operation, sessionId = {}", sessionId);

    try {
      connection.getBotRequestHelper().continueSession(message, sessionId, messageSequenceNumber, callback);
    } catch (Exception e) {
      callback.error(new ModuleException("Error in continue agent conversation for session id: " + sessionId,
                                         AGENT_OPERATIONS_FAILURE, e));
    }
  }

  @MediaType(value = TEXT_PLAIN, strict = false)
  @Alias("End-agent-conversation")
  @Throws(BotErrorTypeProvider.class)
  @OutputResolver(output = AgentConversationResponseMetadataResolver.class)
  public void endConversation(@Connection AgentforceConnection connection,
                              @Content String sessionId,
                              CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback) {

    log.info("Executing end agent conversation operation. sessionId = {}", sessionId);

    try {
      connection.getBotRequestHelper().endSession(sessionId, callback);
    } catch (Exception e) {
      callback.error(new ModuleException("Error in end agent conversation for session id: " + sessionId,
                                         AGENT_OPERATIONS_FAILURE, e));
    }
  }
}
