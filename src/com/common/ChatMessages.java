package com.common;

import java.util.List;

import com.common.work.MessageWork;

public class ChatMessages
{
    public String chatId=null;
    public List<MessageWork> messages=null;

    public ChatMessages(String chatId, List<MessageWork> messages){
        this.chatId = chatId;
        this.messages = messages;
    }
    
    public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public List<MessageWork> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageWork> messages) {
		this.messages = messages;
	}

	public boolean equals(Object other){
        if (!(other instanceof ChatMessages)){
            return false;
        }
        ChatMessages otherCm = (ChatMessages)other;
        return chatId.equals(otherCm.chatId) &&
            messages.equals(otherCm.messages);
    }
}
