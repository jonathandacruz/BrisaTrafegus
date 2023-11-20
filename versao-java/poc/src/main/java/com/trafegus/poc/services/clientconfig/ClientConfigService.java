package com.trafegus.poc.services.clientconfig;

import com.trafegus.poc.model.ClientConfig;

import java.util.List;
import java.util.UUID;

public interface ClientConfigService {

    List<ClientConfig> findAll(String empresaCNPJ);
    ClientConfig findOne(UUID id);
    ClientConfig createOne(ClientConfig clientConfig);
    Boolean deleteOne(UUID id);

}
