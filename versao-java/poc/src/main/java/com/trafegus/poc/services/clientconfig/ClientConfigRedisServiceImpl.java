package com.trafegus.poc.services.clientconfig;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.repository.ClientConfigRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class ClientConfigRedisServiceImpl implements ClientConfigRedisService {

    @Autowired
    private ClientConfigRedisRepository clientConfigRedisRepository;

    @Override
    public Iterable<ClientConfigRedis> findAll() {
        return clientConfigRedisRepository.findAll();
    }

    @Override
    public ClientConfigRedis findOne(UUID id) {
        return clientConfigRedisRepository.findById(id).orElse(null);
    }

    @Override
    public ClientConfigRedis createOne(ClientConfig clientConfig) {
        ClientConfigRedis clientConfigRedis = new ClientConfigRedis();
        clientConfigRedis.setId(clientConfig.getEmpresaId());
        clientConfigRedis.setCodigosImportantes(new ArrayList<>());
        clientConfig.getRegras().forEach(regra -> regra.getCodigos().forEach(codigo -> clientConfigRedis.getCodigosImportantes().add(codigo)));
        return clientConfigRedisRepository.save(clientConfigRedis);
    }

    @Override
    public ClientConfigRedis updateOne(ClientConfig clientConfig, ClientConfigRedis existingRedisConfig) {
        clientConfig.getRegras().forEach(regra -> regra.getCodigos().forEach(codigo -> {
            if (!existingRedisConfig.getCodigosImportantes().contains(codigo)) {
                existingRedisConfig.getCodigosImportantes().add(codigo);
            }
        }));

        return clientConfigRedisRepository.save(existingRedisConfig);
    }

}
