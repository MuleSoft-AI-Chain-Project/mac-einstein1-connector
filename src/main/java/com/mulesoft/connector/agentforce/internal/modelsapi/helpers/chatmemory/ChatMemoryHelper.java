package com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory;

import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsModelDetails;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ChatMemoryHelper {

  private RequestHelper requestHelper;

  public ChatMemoryHelper(RequestHelper requestHelper) {
    this.requestHelper = requestHelper;
  }

  public InputStream chatWithMemory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages,
                                    ParamsModelDetails parameters)
      throws IOException {

    // Chat memory initialization
    ChatMemoryUtil chatMemory = intializeChatMemory(memoryPath, memoryName);

    // Get last Messages To Keep
    List<String> lastMessages = getLastMessagesToKeep(chatMemory, keepLastMessages);
    lastMessages.add(prompt);
    String memoryPrompt = formatMemoryPrompt(lastMessages);

    InputStream response = requestHelper.executeGenerateText(memoryPrompt, parameters);

    addMessageToMemory(chatMemory, prompt);

    return response;
  }

  private ChatMemoryUtil intializeChatMemory(String memoryPath, String memoryName) {
    return new ChatMemoryUtil(memoryPath, memoryName);
  }

  private List<String> getLastMessagesToKeep(ChatMemoryUtil chatMemory, Integer keepLastMessages) {

    // Retrieve all messages in ascending order of messageId
    List<String> messages = chatMemory.getAllMessagesByMessageIdAsc();

    // Keep only the last index messages
    if (messages.size() > keepLastMessages) {
      messages = messages.subList(messages.size() - keepLastMessages, messages.size());
    }
    return messages;
  }

  private void addMessageToMemory(ChatMemoryUtil chatMemory, String prompt) {
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
