package com.mule.mulechain.internal.helpers.chatmemory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.runtime.extension.internal.MuleDsqlParser.relation_return;

import com.mule.mulechain.internal.MuleChainEinstein1Configuration;
import com.mule.mulechain.internal.helpers.MuleChainEinstein1PayloadHelper;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsModelDetails;

import java.util.List;

public class MuleChainEinstein1ChatMemoryHelper {


  private static MuleChainEinstein1ChatMemory intializeChatMemory(String memoryPath, String memoryName) {
    return new MuleChainEinstein1ChatMemory(memoryPath, memoryName);
  }

  private static List<String> getKeepLastMessage(MuleChainEinstein1ChatMemory chatMemory, Integer keepLastMessages){

    // Retrieve all messages in ascending order of messageId
    List<String> messagesAsc = chatMemory.getAllMessagesByMessageIdAsc();

    // Keep only the last index messages
    if (messagesAsc.size() > keepLastMessages) {
        messagesAsc = messagesAsc.subList(messagesAsc.size() - keepLastMessages, messagesAsc.size());
    }

    return messagesAsc;
    
  }

  private static void addMessageToMemory(MuleChainEinstein1ChatMemory chatMemory, String prompt){
    if (!isQuestion(prompt)) {
        chatMemory.addMessage(chatMemory.getMessageCount() + 1, prompt);
    }
  }

  private static boolean isQuestion(String message) {
    // Check if the message ends with a question mark
    if (message.trim().endsWith("?")) {
        return true;
    }
    // Check if the message starts with a question word (case insensitive)
    String[] questionWords = {"who", "what", "when", "where", "why", "how", "tell", "tell me", "do you", };
    String lowerCaseMessage = message.trim().toLowerCase();
    for (String questionWord : questionWords) {
        if (lowerCaseMessage.startsWith(questionWord + " ")) {
            return true;
        }
    }
    return false;
}

  private static String formatMemoryPrompt(List<String> messages) {
    StringBuilder formattedPrompt = new StringBuilder();
    for (String message : messages) {
        formattedPrompt.append(message).append("\n");
    }
    return formattedPrompt.toString().trim();
}


  public static String chatWithMemory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParamsModelDetails MuleChainParameters) {

    //Chatmemory initialization
    MuleChainEinstein1ChatMemory chatMemory = intializeChatMemory(memoryPath, memoryName);

    //Get keepLastMessages
    List<String> keepLastMessagesList = getKeepLastMessage(chatMemory, keepLastMessages);
    keepLastMessagesList.add(prompt);
    //String memoryPrompt = keepLastMessagesList.toString();
    String memoryPrompt = formatMemoryPrompt(keepLastMessagesList);
    
    String response = MuleChainEinstein1PayloadHelper.executeGenerateText(memoryPrompt, configuration, MuleChainParameters);
    
    addMessageToMemory(chatMemory, prompt);

    return response;
}


}