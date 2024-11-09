package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String message; //Whole message
    private String body;    //Body
    private String type;    //Header containing CMD or CHAT
    private LocalDateTime timestamp;
    private String client;

    // Constructor
    //Recibe mensaje y cliente
    // Mensaje type CHAT o CMD
    //CHAT: mandarlo a tabla hash!!!!= mensaje y marina hace split
    //CMD: mandarlo a tabla hash!!!!= mensaje y marina hace split
    public Message(String message, String client) {
        this.timestamp = LocalDateTime.now();
        this.client = client;
        this.message = message;
    }

    //Constructor para mensajes del servidor
    public Message(String message) {
        this.timestamp =  LocalDateTime .now();
        this.client = "";
        this.message = message;
    }

    public String parseMessage() {
        String[] parts = this.message.split("\\|", 2);
        this.type = parts[0];
        this.body = parts.length > 1 ? parts[1] : "";
    
        switch (this.type) {
            case "CHAT" -> {
                return getMessage();  // Display the message in the chat
            }
            case "CMD" -> {
                return executeCommand();  // Execute the command, don’t show in chat
            }
            default -> { return ""; } 
        }
    }
    
    //INPUT:.getUawe))zzz
    //CHAT|HelloWorld!
    private String getMessage() {
        if (this.client.isBlank()) {
            return "[" + this.timestamp + "] Server:" +this.body;
        } else if (this.message.contains("@")) {
            return this.client+": "+this.body;
        } 
        return "[" + this.timestamp + "] "+ this.client + ": "+this.body;
    }
    //OUTPUT:
    //[Timestamp] Username: Hellowordl!
    
    private String executeCommand() {
        if (this.client.isBlank()) {    //Commands coming from the server
            if (isNewUserCommand()) {
                return "SERVER|0";
            } else if (isGameStartCommand()) {
                return "SERVER|1";
            } else if (isGameAcceptCommand()) {
                return "SERVER|2";
            } else if (isGameDeclineCommand()) {
                return "SERVER|3";
            }
        } else {    //Commands from client
            if (isNewUserCommand()) {
                return "CLI|0";
            } else if (isNewList()) {
                return "CLI|1";
            } else if (!doRefresh().isBlank()) {
                return "CLI|2"+doRefresh();
            } else if (isDoneRefreshing()) {
                return "CLI|3";
            } else if (isWordle()) {
                return "CLI|4";
            } else if (isContinueWordle()) {
                return "CLI|5";
            }
        }
        return "";
    }
    

    //CMD|newUser Username
    


    //TODO Crear un switch case para los tipos de comandos y que responder en caso de recibirlos.

    public String getBody() {
        return body;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getClient(){
        return this.client;
    }

    // Format timestamp for display
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return timestamp.format(formatter);
    }

    // Check if message is a specific type
    public boolean isNewUserCommand() {
        return body.contains("NEWUSER");
    }

    public String doRefresh() {
        if(body.contains("REFRESH")){
            return body;
        }
        return "";
    }
    public boolean isNewList() {
        return body.contains("NEWLIST");
    }

    public boolean isDoneRefreshing() {
        return body.contains("DONEREFRESHING");
    }

    public boolean isGameStartCommand() {
        return body.contains("GAME1");
    }

    public boolean isWordle() {
        return body.contains("STARTWORDLE");
    }
    public boolean isContinueWordle() {
        return body.contains("CONTINUEWORDLE");
    }
    public boolean isGameAcceptCommand() {
        return body.contains("GAME1ACCEPT");
    }
    public boolean isGameDeclineCommand() {
        return body.contains("GAME1DECLINE");
    }
}
