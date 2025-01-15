/**
 * (c) 2003-2023 MuleSoft, Inc. The software in this package is
 * published under the terms of the Commercial Free Software license V.1, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */
package com.mulesoft.connector.agentforce.internal.botapi.metadata;

import com.mulesoft.connector.agentforce.internal.botapi.dto.BotRecord;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelperImpl;
import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.mule.runtime.extension.api.values.ValueResolvingException.CONNECTION_FAILURE;

public class AgentListValueProvider implements ValueProvider {

  private static final String AGENT_LIST_ERR_MSG =
      "An error occurred while fetching agent list";

  @Connection
  AgentforceConnection connection;

  @Override
  public Set<Value> resolve() throws ValueResolvingException {
    try {

      return new BotRequestHelperImpl(connection).getAgentList()
          .stream()
          .filter(agent -> agent.getStatus().equals("Active"))
          .map(agent -> ValueBuilder
              .newValue(agent.getBotDefinitionId())
              .withDisplayName(constructDisplayName(agent))
              .build())
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new ValueResolvingException(format(AGENT_LIST_ERR_MSG), CONNECTION_FAILURE, e);
    }
  }

  private static String constructDisplayName(BotRecord agent) {
    return agent.getBotDefinition().getMasterLabel() + " [" + agent.getBotDefinitionId() + "]";
  }
}
