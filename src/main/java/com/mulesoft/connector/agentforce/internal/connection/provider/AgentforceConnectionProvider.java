package com.mulesoft.connector.agentforce.internal.connection.provider;

import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connectors.commons.template.connection.provider.ConnectorConnectionProvider;

//In future if we are adding new connection provider, we will make this abstract class and common parameters of
// connection providers will go here
public interface AgentforceConnectionProvider extends ConnectorConnectionProvider<AgentforceConnection> {
}
