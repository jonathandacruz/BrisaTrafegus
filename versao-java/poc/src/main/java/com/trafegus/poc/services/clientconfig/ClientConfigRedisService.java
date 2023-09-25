package com.trafegus.poc.services.clientconfig;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;

import java.util.UUID;

public interface ClientConfigRedisService {

    Iterable<ClientConfigRedis> findAll();
    ClientConfigRedis findOne(UUID id);
    ClientConfigRedis createOne(ClientConfig clientConfig);

    ClientConfigRedis updateOne(ClientConfig clientConfig, ClientConfigRedis existingRedisConfig);

    Boolean deleteOneConfig(UUID id, ClientConfig clientConfig);
}
