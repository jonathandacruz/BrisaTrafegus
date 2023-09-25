package com.trafegus.poc.services.clientconfig;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.repository.ClientConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientConfigServiceImpl implements ClientConfigService {

    @Autowired
    private ClientConfigRepository clientConfigRepository;

    @Override
    public List<ClientConfig> findAll() {
        return clientConfigRepository.findAll();
    }

    @Override
    public ClientConfig findOne(UUID id) {
        return clientConfigRepository.findById(id).orElse(null);
    }

    @Override
    public ClientConfig createOne(ClientConfig clientConfig) {
        clientConfig.setId(UUID.randomUUID());
        return clientConfigRepository.save(clientConfig);
    }

    @Override
    public Boolean deleteOne(UUID id) {
        try {
            clientConfigRepository.deleteById(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
