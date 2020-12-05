package com.sumitkumarojha.bot.controller;

import com.sumitkumarojha.bot.dto.MessageDTO;
import com.sumitkumarojha.bot.manager.BotManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


@RestController
@RequiredArgsConstructor
public class BotControllerImpl implements BotController{

    private final BotManager botManager;

    @Override
    public ResponseEntity<String> getCovidData(MessageDTO messageDTO) {
        if(Objects.nonNull(messageDTO) && StringUtils.isNoneBlank(messageDTO.getFrom()) && StringUtils.isNoneBlank(messageDTO.getBody()) ){
            String result = botManager.getCovidData(messageDTO);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Passed Parameter might be null or empty", HttpStatus.BAD_REQUEST);
        }
    }
}
