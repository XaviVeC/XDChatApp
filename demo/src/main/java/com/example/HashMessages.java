package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMessages {
    private Map<String, List<String>> hashTable;

    public HashMessages() {
        this.hashTable = new HashMap<>();
    }

    public void addUserMessage(String username, String message) {
        hashTable.putIfAbsent(username, new ArrayList<>()); // Añade el usuario si no existe
        hashTable.get(username).add(message); // Añade el mensaje a la lista del usuario
    }

    // Obtener los mensajes de un usuario
    public List<String> getUserMessages(String username) {
        return hashTable.getOrDefault(username, new ArrayList<>()); // Devuelve la lista o una nueva si no existe
    }
}
