package com.mulesoft.connector.agentforce.internal.botapi.helpers;

import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;

import java.io.IOException;

public interface BotRequestHelper {

  String getAgentList() throws IOException;

  String startSession(String agentId) throws IOException;

  String continueSession(String message, String sessionId) throws IOException;

  String endSession(String sessionId) throws IOException;
}
