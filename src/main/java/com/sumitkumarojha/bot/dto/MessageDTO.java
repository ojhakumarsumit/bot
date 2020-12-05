package com.sumitkumarojha.bot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {
    private String From;
    private String To;
    private String Body;
}
