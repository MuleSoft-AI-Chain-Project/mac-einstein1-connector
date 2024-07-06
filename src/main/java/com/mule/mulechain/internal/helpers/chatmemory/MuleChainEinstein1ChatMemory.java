package com.mule.mulechain.internal.helpers.chatmemory;

import org.mapdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MuleChainEinstein1ChatMemory {
    private DB db;
    private Map<Long, String> chatMap;

    public MuleChainEinstein1ChatMemory(String dbFile, String memoryName) {
        // Open or create the DB file
        db = DBMaker.fileDB(new File(dbFile))
                .transactionEnable()
                .fileLockDisable()
                .make();

        // Create or retrieve the chat map
        chatMap = db.hashMap(memoryName)
                .keySerializer(Serializer.LONG)
                .valueSerializer(Serializer.STRING)
                .createOrOpen();
    }

    public void addMessage(long messageId, String messageContent) {
        chatMap.put(messageId, messageContent);
        db.commit(); // Save changes
    }

    public void deleteMessage(long messageId) {
        chatMap.remove(messageId);
        db.commit(); // Save changes
    }

    public void deleteAllMessages() {
        chatMap.clear();
        db.commit(); // Save changes
    }

    public String getMessage(long messageId) {
        return chatMap.get(messageId);
    }

    public int getMessageCount() {
        return chatMap.size();
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(chatMap.values());
    }

    public List<String> getAllMessagesByMessageIdDesc() {
        // Retrieve all messageIds and sort them in descending order
        List<Long> messageIds = new ArrayList<>(chatMap.keySet());
        messageIds.sort(Comparator.reverseOrder());

        // Retrieve messages in descending order of messageId
        List<String> messages = new ArrayList<>();
        for (long messageId : messageIds) {
            messages.add(chatMap.get(messageId));
        }
        return messages;
    }

    public List<String> getAllMessagesByMessageIdAsc() {
        // Retrieve all messageIds and sort them in ascending order
        List<Long> messageIds = new ArrayList<>(chatMap.keySet());
        messageIds.sort(Comparator.naturalOrder());

        // Retrieve messages in ascending order of messageId
        List<String> messages = new ArrayList<>();
        for (long messageId : messageIds) {
            messages.add(chatMap.get(messageId));
        }
        return messages;
    }


    public void close() {
        db.close();
    }
}
