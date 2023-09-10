package com.trafegus.poc.services.poc;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.model.Log;
import com.trafegus.poc.repository.ClientConfigRedisRepository;
import com.trafegus.poc.repository.ClientConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PocServiceImpl implements PocService{

    @Autowired
    private ClientConfigRepository clientConfigRepository;

    @Autowired
    private ClientConfigRedisRepository clientConfigRedisRepository;

    @Override
    public Boolean processLog(Log logRecebido) {

        Optional<ClientConfigRedis> clientConfigRedis = this.clientConfigRedisRepository.findById(logRecebido.getEmpresaId());

        if (clientConfigRedis.isPresent()) {
            ClientConfigRedis presentClientConfigRedis = clientConfigRedis.get();
            List<ClientConfig> clientConfigs = this.clientConfigRepository.findClientConfigByRegrasContaining(logRecebido.getCodigo());
            log.info(clientConfigs.toString());
        }

        return true;
    }

}
