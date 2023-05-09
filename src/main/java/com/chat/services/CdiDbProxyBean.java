package com.chat.services;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("proxy")
@SessionScoped
public class CdiDbProxyBean implements Serializable{

    private String chatContent;

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;

    }

    public String getChatContent() {
        return chatContent;
    }
    public String redirect(){
        return "chatclient.xhtml";
    }
}
