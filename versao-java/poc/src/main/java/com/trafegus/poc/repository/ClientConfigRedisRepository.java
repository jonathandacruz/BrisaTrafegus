package com.trafegus.poc.repository;

import com.trafegus.poc.model.ClientConfigRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientConfigRedisRepository extends CrudRepository<ClientConfigRedis, String> {
}
