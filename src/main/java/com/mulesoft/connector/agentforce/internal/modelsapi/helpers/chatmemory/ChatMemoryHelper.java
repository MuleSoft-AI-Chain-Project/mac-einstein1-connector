package com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory;

import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsModelDetails;

import java.io.IOException;

public interface ChatMemoryHelper {

  String chatWithMemory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages,
                        ParamsModelDetails parameters)
      throws IOException;
}
