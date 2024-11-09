package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMessages {
    private Map<String, List<Message>> hashTable;

    public HashMessages() {
        this.hashTable = new HashMap<>();
    }

    public void addUserMessage(String username, Message message) {
        hashTable.putIfAbsent(username, new ArrayList<>());
        hashTable.get(username).add(message);
    }

    public void addMessage(Message message) {
        addUserMessage(message.getClient(), message);
          
    }

    public List<Message> getUserMessages(String username) {
        return hashTable.getOrDefault(username, new ArrayList<>());
    }
}
