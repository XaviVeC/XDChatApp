// package com.example;

// import java.util.HashMap;
// import java.util.Map;

// public class HashClients {
//     private Map<String, String> clientCredentials;

//     public HashClients() {
//         this.clientCredentials = new HashMap<>();
//     }

//     public void addClient(Client client) {
//         String username = client.getUsername();
//         String password = client.getPassword();
//         clientCredentials.put(username, password);
//     }

//     public boolean clientExists(String username) {
//         return clientCredentials.containsKey(username);
//     }

//     public boolean authenticateClient(String username, String password) {
//         return clientCredentials.getOrDefault(username, "").equals(password);
//     }

//     public String getClientPassword(String username) {
//         return clientCredentials.get(username);
//     }

//     public void removeClient(String username) {
//         clientCredentials.remove(username);
//     }
// }
