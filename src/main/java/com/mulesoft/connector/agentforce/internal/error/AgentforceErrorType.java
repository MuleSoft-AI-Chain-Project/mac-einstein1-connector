package com.mulesoft.connector.agentforce.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public enum AgentforceErrorType implements ErrorTypeDefinition<AgentforceErrorType> {
  AGENT_METADATA_FAILURE, AGENT_OPERATIONS_FAILURE, AGENT_API_ERROR, INVALID_CONNECTION
}
