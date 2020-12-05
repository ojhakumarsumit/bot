package com.sumitkumarojha.bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CovidData implements Serializable {

    private UUID covidKey;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Global")
    private CovidGlobalData global;
    @JsonProperty("Countries")
    private List<CovidCountryData> countries;
    @JsonProperty("Date")
    private String date;
    private Long expirationDate;

}

