package io.openlight.response;


import java.util.ArrayList;

public class ErrorResponse {

    public ArrayList<String> messages;

    public void addMessage(String message){
        if(messages==null) messages = new ArrayList<>();
        messages.add(message);
    }
}
