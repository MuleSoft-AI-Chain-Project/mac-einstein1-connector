package com.mulesoft.connector.einstein.internal.connection;

import com.mulesoft.connector.einstein.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.einstein.internal.modelsapi.helpers.chatmemory.ChatMemoryHelper;
import com.mulesoft.connectors.commons.template.connection.ConnectorConnection;

/**
 * This class represents a connection to the external system.
 */
// In future if we are adding new connection types, then common parameters of connection types will go here
public interface EinsteinConnection extends ConnectorConnection {

  String getApiInstanceUrl();

  RequestHelper getRequestHelper();

  ChatMemoryHelper getChatMemoryHelper();

  String getAccessToken();
}
