package com.sumitkumarojha.bot.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGenerateStrategy.CREATE;
import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType.L;
import static org.springframework.beans.BeanUtils.copyProperties;

@DynamoDBTable(tableName = "Covid")
@NoArgsConstructor
public class Covid extends CovidData {

    public Covid(CovidData theCovidData) {
        super();
        copyProperties(theCovidData, this);
    }

    @Id
    @DynamoDBHashKey
    @DynamoDBGeneratedUuid(CREATE)
    @DynamoDBAttribute
    @Override
    public UUID getCovidKey(){
        return super.getCovidKey();
    }

    @DynamoDBAttribute
    @Override
    public CovidGlobalData getGlobal(){
        return super.getGlobal();
    }

    @DynamoDBAttribute
    @Override
    public String getMessage(){
        return super.getMessage();
    }

    @DynamoDBAttribute
    @DynamoDBTyped(L)
    @Override
    public List<CovidCountryData> getCountries(){
        return super.getCountries();
    }

    @DynamoDBAttribute
    @Override
    public String getDate(){
        return super.getDate();
    }

    @DynamoDBAttribute
    @Override
    public Long getExpirationDate(){
        return super.getExpirationDate();
    }

    public CovidData toCovidData(){
        CovidData covidData = new CovidData();
        copyProperties(this, covidData);
        return covidData;
    }
}
