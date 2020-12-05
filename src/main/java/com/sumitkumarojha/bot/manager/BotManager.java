package com.sumitkumarojha.bot.manager;

import com.amazonaws.util.CollectionUtils;
import com.sumitkumarojha.bot.dto.MessageDTO;
import com.sumitkumarojha.bot.model.Covid;
import com.sumitkumarojha.bot.model.CovidCountryData;
import com.sumitkumarojha.bot.model.CovidData;
import com.sumitkumarojha.bot.property.CovidProperty;
import com.sumitkumarojha.bot.property.TwilioProperty;
import com.sumitkumarojha.bot.repository.CovidRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.stream.StreamSupport;

import static com.sumitkumarojha.bot.utils.Constants.*;

@Component
@Service
@RequiredArgsConstructor
public class BotManager {

    private final RestTemplate restTemplate;
    private final CovidRepository covidRepository;
    private final TwilioProperty twilioProperty;
    private final CovidProperty covidProperty;

    public String getCovidData(MessageDTO messageDTO) {
        final var message = getCovidDataWithTheMessage(messageDTO.getBody(), getCovidResults());
        postMessageToWhatsapp(message, messageDTO.getFrom());
        return message;
    }

    /**
     * To send aggregated message with data/error to the user
     * @param body message with data/error
     * @param recipient the user who made query
     */
    public void postMessageToWhatsapp(String body, String recipient) {
        Twilio.init(twilioProperty.getAccountSid(), twilioProperty.getAuthToken());
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(recipient),
                new com.twilio.type.PhoneNumber(twilioProperty.getContact()),
                body)
                .create();
        System.out.println(message.getSid());
    }

    /**
     * makes call to the api if data is not already stored in the database
     * @return Covid data from api or fetched from the database
     */
    public CovidData getCovidResults(){
        var count = StreamSupport.stream(this.covidRepository.findAll().spliterator(), false).count();
        //if count zero then database is empty
        if(count == 0) {
            return getCovidDataFromApiCall();
        } else {
            //get data from database
            return StreamSupport.stream(this.covidRepository.findAll().spliterator(), false).findFirst().get().toCovidData();
        }
    }

    /**
     * Logic to check the validity of the query
     * Accepts CASES <country code> , CASES TOTAL, DEATHS <country code> and DEATHS TOTAL
     * Throws error for queries other than these.
     * @param message query by the user
     * @param covidData data fetched from the API or Database
     * @return query results/error
     */
    public String getCovidDataWithTheMessage(String message, CovidData covidData){
        final var words = message.trim().split(EMPTY_STRING);
        if(words.length != ACCEPTED_WORD_LENGTH){
            return WRONG_INPUT;
        } else {
            String firstWord = words[0].toUpperCase();
            String secondWord = words[1].toUpperCase();
            switch (firstWord) {
                case "CASES":
                    return getMessageWithActiveCases(secondWord, covidData);
                case "DEATHS":
                    return getMessageWithDeathCounts(secondWord, covidData);
                default:
                    return ERROR;
            }
        }
    }

    /**
     * fetches Covid data from API and saves it in Database with expiration time
     * The Items of the Database are deleted once the expiration time matches with current time.(Time to Live)
     * @return
     */
    public CovidData getCovidDataFromApiCall(){
        CovidData covidData = restTemplate.getForObject(covidProperty.getApi(), CovidData.class);
        assert covidData != null;
        try {
            final var date = DateUtils.parseDate(covidData.getDate(), "yyyy-MM-dd HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss'Z'");
            final var expireDate = DateUtils.addDays(date, 1);
            covidData.setExpirationDate(expireDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Store data in db only if the API has fetched all the data and is not caching the data
        if(StringUtils.isBlank(covidData.getMessage())){
            this.covidRepository.save(new Covid(covidData));
        }
        return covidData;
    }

    /**
     * Calculates data for TOTAL ACTIVE CASES AND CASES <country code>
     * returns error messages for invalid queries and API caching data
     * @param code query code
     * @param covidData data fetched from db/API
     * @return aggregated data
     */
    public String getMessageWithActiveCases(String code, CovidData covidData){
        String totalActive;
        if (code.equals(TOTAL)) {
            totalActive = String.valueOf(covidData.getGlobal().getTotalConfirmed() - (covidData.getGlobal().getTotalDeaths() + covidData.getGlobal().getTotalRecovered()));
            return TOTAL_ACTIVE + totalActive;
        } else if(!CollectionUtils.isNullOrEmpty(covidData.getCountries()) && covidData.getCountries().stream().anyMatch(a -> a.getCountryCode().equals(code))){
            final var covidCountryData = covidData.getCountries().stream().filter(f -> f.getCountryCode().equals(code)).findFirst().orElse(null);
            assert covidCountryData != null;
            totalActive = String.valueOf(covidCountryData.getTotalConfirmed() - (covidCountryData.getTotalDeaths() + covidCountryData.getTotalRecovered()));
            return TOTAL_ACTIVE_CASES_IN_COUNTRY + covidCountryData.getCountry().toUpperCase() + COLON + totalActive;
        } else if(StringUtils.isNoneBlank(covidData.getMessage())) {
            return covidData.getMessage();
        } else {
            return ERROR;
        }
    }

    /**
     * Calculates data for TOTAL DEATHS AND DEATHS <country code>
     * returns error messages for invalid queries and API caching data
     * @param code query code
     * @param covidData data fetched from db/API
     * @return aggregated data
     */
    public String getMessageWithDeathCounts(String code, CovidData covidData) {
        String totalDeaths;
        if (code.equals(TOTAL)) {
            totalDeaths = String.valueOf(covidData.getGlobal().getTotalDeaths());
            return TOTAL_DEATH + totalDeaths;
        } else if(!CollectionUtils.isNullOrEmpty(covidData.getCountries()) && covidData.getCountries().stream().anyMatch(a -> a.getCountryCode().equals(code))){
            CovidCountryData covidCountryData = covidData.getCountries().stream().filter(f -> f.getCountryCode().equals(code)).findFirst().orElse(null);
            assert covidCountryData != null;
            totalDeaths = String.valueOf(covidCountryData.getTotalDeaths());
            return TOTAL_DEATHS_IN_COUNTRY + covidCountryData.getCountry().toUpperCase() + COLON + totalDeaths;
        } else if(StringUtils.isNoneBlank(covidData.getMessage())) {
            return covidData.getMessage();
        } else {
            return ERROR;
        }
    }
}
