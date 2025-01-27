package com.mulesoft.connector.agentforce.internal.error.provider;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

import static com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType.AGENT_METADATA_FAILURE;
import static com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType.AGENT_OPERATIONS_FAILURE;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public class BotErrorTypeProvider implements ErrorTypeProvider {

  @SuppressWarnings("rawtypes")
  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return unmodifiableSet(new HashSet<>(asList(AGENT_METADATA_FAILURE, AGENT_OPERATIONS_FAILURE)));
  }
}
