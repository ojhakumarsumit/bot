package com.sumitkumarojha.bot.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "twilio")
public class TwilioProperty {
    private String accountSid;
    private String authToken;
    private String contact;
}
