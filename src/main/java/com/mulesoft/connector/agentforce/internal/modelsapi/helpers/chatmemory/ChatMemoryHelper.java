package com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory;

import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsModelDetails;

import java.io.IOException;
import java.util.List;

public class ChatMemoryHelper {

  private RequestHelper requestHelper;

  public ChatMemoryHelper(RequestHelper requestHelper) {
    this.requestHelper = requestHelper;
  }

  public String chatWithMemory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages,
                               ParamsModelDetails parameters)
      throws IOException {

    //Chat memory initialization
    ChatMemoryUtil chatMemory = intializeChatMemory(memoryPath, memoryName);

    //Get keepLastMessages
    List<String> keepLastMessagesList = getKeepLastMessage(chatMemory, keepLastMessages);
    keepLastMessagesList.add(prompt);
    String memoryPrompt = formatMemoryPrompt(keepLastMessagesList);

    String response = requestHelper.executeGenerateText(memoryPrompt, parameters);

    addMessageToMemory(chatMemory, prompt);

    return response;
  }

  private ChatMemoryUtil intializeChatMemory(String memoryPath, String memoryName) {
    return new ChatMemoryUtil(memoryPath, memoryName);
  }

  private List<String> getKeepLastMessage(ChatMemoryUtil chatMemory, Integer keepLastMessages) {

    // Retrieve all messages in ascending order of messageId
    List<String> messagesAsc = chatMemory.getAllMessagesByMessageIdAsc();

    // Keep only the last index messages
    if (messagesAsc.size() > keepLastMessages) {
      messagesAsc = messagesAsc.subList(messagesAsc.size() - keepLastMessages, messagesAsc.size());
    }
    return messagesAsc;
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
