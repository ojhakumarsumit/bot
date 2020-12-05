package com.sumitkumarojha.bot.repository;

import com.sumitkumarojha.bot.model.Covid;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CovidRepository extends CrudRepository<Covid, UUID> {
    Optional<Covid> findById(UUID id);

    @EnableScan
    Iterable<Covid> findAll();
}
