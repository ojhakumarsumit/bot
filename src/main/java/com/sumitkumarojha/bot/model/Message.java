package com.sumitkumarojha.bot.model;

import com.sumitkumarojha.bot.dto.MessageDTO;

public class Message extends MessageDTO {

    public String getFrom(){
        return super.getFrom();
    }

    public String getTo(){
        return super.getTo();
    }

    public String getBody(){
        return super.getBody();
    }
}
