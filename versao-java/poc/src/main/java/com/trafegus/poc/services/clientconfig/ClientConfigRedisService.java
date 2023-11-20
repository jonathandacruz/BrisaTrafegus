package com.trafegus.poc.services.clientconfig;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;

public interface ClientConfigRedisService {

    Iterable<ClientConfigRedis> findAll();
    ClientConfigRedis findOne(String id);
    ClientConfigRedis createOne(ClientConfig clientConfig);

    ClientConfigRedis updateOne(ClientConfig clientConfig, ClientConfigRedis existingRedisConfig);

    Boolean deleteOneConfig(String id, ClientConfig clientConfig);
}
