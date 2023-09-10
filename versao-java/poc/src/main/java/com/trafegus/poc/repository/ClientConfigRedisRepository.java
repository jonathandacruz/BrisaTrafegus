package com.trafegus.poc.repository;

import com.trafegus.poc.model.ClientConfigRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.UUID;

@Repository
public interface ClientConfigRedisRepository extends CrudRepository<ClientConfigRedis, UUID> {
}
