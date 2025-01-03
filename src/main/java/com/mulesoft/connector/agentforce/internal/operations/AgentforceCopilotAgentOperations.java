package com.mulesoft.connector.agentforce.internal.operations;

import com.mulesoft.connector.agentforce.api.metadata.AgentforceResponseAttributes;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.error.provider.ChatErrorTypeProvider;
import com.mulesoft.connector.agentforce.internal.helpers.PayloadHelper;
import com.mulesoft.connector.agentforce.internal.helpers.PromptTemplateHelper;
import com.mulesoft.connector.agentforce.internal.helpers.ResponseHelper;
import com.mulesoft.connector.agentforce.internal.models.CopilotAgentDetails;
import com.mulesoft.connector.agentforce.internal.models.ParamsModelDetails;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType.CHAT_FAILURE;
import static java.lang.String.format;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

public class AgentforceCopilotAgentOperations {

  private static final Logger log = LoggerFactory.getLogger(AgentforceCopilotAgentOperations.class);

  PayloadHelper payloadHelper = new PayloadHelper();

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("copilot-create-session")
  @Throws(ChatErrorTypeProvider.class)
  public String createSession(@Connection AgentforceConnection connection,
                              @ParameterGroup(name = "Additional properties") CopilotAgentDetails copilotAgentDetails)
      throws IOException {

    System.out.println("copilotAgentDetails = " + copilotAgentDetails);
    Map<String, String> botNameIdMap = payloadHelper.getAgentMetadata(connection);
    System.out.println(botNameIdMap);
    //return payloadHelper.createSession("0XxdL00000041WzSAI", connection);
    return payloadHelper.createSession(botNameIdMap.get(copilotAgentDetails.getAgent()), connection);

  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("copilot-continue-session")
  @Throws(ChatErrorTypeProvider.class)
  public String continueSession(@Content(primary = true) String body, @Content String sessionId,
                                @Connection AgentforceConnection connection)
      throws IOException {


    return payloadHelper.continueSession(body, sessionId, connection);

  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("copilot-delete-session")
  @Throws(ChatErrorTypeProvider.class)
  public String deleteSession(@Content String sessionId, @Connection AgentforceConnection connection)
      throws IOException {

    return payloadHelper.deleteSession(sessionId, connection);

  }
}
