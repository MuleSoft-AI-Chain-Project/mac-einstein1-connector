package com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory;

import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsModelDetails;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ChatMemoryHelper {

  public InputStream chatWithMemory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages,
                                    AgentforceConnection connection, ParamsModelDetails parameters, RequestHelper requestHelper)
      throws IOException {

    //Chat memory initialization
    ChatMemory chatMemory = intializeChatMemory(memoryPath, memoryName);

    //Get keepLastMessages
    List<String> keepLastMessagesList = getKeepLastMessage(chatMemory, keepLastMessages);
    keepLastMessagesList.add(prompt);
    String memoryPrompt = formatMemoryPrompt(keepLastMessagesList);

    InputStream response = requestHelper.executeGenerateText(memoryPrompt, connection, parameters);

    addMessageToMemory(chatMemory, prompt);

    return response;
  }

  private ChatMemory intializeChatMemory(String memoryPath, String memoryName) {
    return new ChatMemory(memoryPath, memoryName);
  }

  private List<String> getKeepLastMessage(ChatMemory chatMemory, Integer keepLastMessages) {

    // Retrieve all messages in ascending order of messageId
    List<String> messagesAsc = chatMemory.getAllMessagesByMessageIdAsc();

    // Keep only the last index messages
    if (messagesAsc.size() > keepLastMessages) {
      messagesAsc = messagesAsc.subList(messagesAsc.size() - keepLastMessages, messagesAsc.size());
    }
    return messagesAsc;
  }

  private void addMessageToMemory(ChatMemory chatMemory, String prompt) {
    if (!isQuestion(prompt)) {
      chatMemory.addMessage(chatMemory.getMessageCount() + 1L, prompt);
    }
  }

  private boolean isQuestion(String message) {
    // Check if the message ends with a question mark
    if (message.trim().endsWith("?")) {
      return true;
    }
    // Check if the message starts with a question word (case insensitive)
    String[] questionWords = {"who", "what", "when", "where", "why", "how", "tell", "tell me", "do you",};
    String lowerCaseMessage = message.trim().toLowerCase();
    for (String questionWord : questionWords) {
      if (lowerCaseMessage.startsWith(questionWord + " ")) {
        return true;
      }
    }
    return false;
  }

  private String formatMemoryPrompt(List<String> messages) {
    StringBuilder formattedPrompt = new StringBuilder();
    for (String message : messages) {
      formattedPrompt.append(message).append("\n");
    }
    return formattedPrompt.toString().trim();
  }
}
