package com.mulesoft.connector.agentforce.internal.botapi.helpers;

import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentConversationResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotRecord;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface BotRequestHelper {

  List<BotRecord> getAgentList() throws IOException;

  AgentConversationResponseDTO startSession(String agentId) throws IOException;

  AgentConversationResponseDTO continueSession(InputStream message, String sessionId, int messageSequenceNumber,
                                               String inReplyToMessageId)
      throws IOException;

  AgentConversationResponseDTO endSession(String sessionId) throws IOException;
}
