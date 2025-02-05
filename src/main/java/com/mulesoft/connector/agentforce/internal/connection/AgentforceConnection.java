package com.mulesoft.connector.agentforce.internal.connection;

import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelper;
import com.mulesoft.connectors.commons.template.connection.ConnectorConnection;

/**
 * This class represents a connection to the external system.
 */
// In future if we are adding new connection types, then common parameters of connection types will go here
public interface AgentforceConnection extends ConnectorConnection {

  String getSalesforceOrgUrl();

  String getApiInstanceUrl();

  String getOrgId();

  BotRequestHelper getBotRequestHelper();

  String getAccessToken();
}
