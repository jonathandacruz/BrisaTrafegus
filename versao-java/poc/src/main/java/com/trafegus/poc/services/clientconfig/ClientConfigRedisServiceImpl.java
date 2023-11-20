package com.trafegus.poc.services.clientconfig;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.repository.ClientConfigRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ClientConfigRedis findOne(String id) {
        return clientConfigRedisRepository.findById(id).orElse(null);
    }

    @Override
    public ClientConfigRedis createOne(ClientConfig clientConfig) {
        ClientConfigRedis clientConfigRedis = new ClientConfigRedis();
        clientConfigRedis.setId(clientConfig.getEmpresaCNPJ());
        clientConfigRedis.setCodigosImportantes(new ArrayList<>());
        clientConfig.getRegras().forEach(regra -> regra.getCodigos().forEach(codigo -> clientConfigRedis.getCodigosImportantes().add(codigo)));
        clientConfigRedis.setConfiguracoes(List.of(clientConfig));
        return clientConfigRedisRepository.save(clientConfigRedis);
    }

    @Override
    public ClientConfigRedis updateOne(ClientConfig clientConfig, ClientConfigRedis existingRedisConfig) {
        existingRedisConfig.getConfiguracoes().add(clientConfig);
        clientConfig.getRegras().forEach(regra -> regra.getCodigos().forEach(codigo -> {
            if (!existingRedisConfig.getCodigosImportantes().contains(codigo)) {
                existingRedisConfig.getCodigosImportantes().add(codigo);
            }
        }));

        return clientConfigRedisRepository.save(existingRedisConfig);
    }

    @Override
    public Boolean deleteOneConfig(String id, ClientConfig clientConfig) {
        Optional<ClientConfigRedis> clientConfigRedisOptional = clientConfigRedisRepository.findById(id);
        if (clientConfigRedisOptional.isPresent()) {
            ClientConfigRedis clientConfigRedis = clientConfigRedisOptional.get();
            log.info(clientConfigRedis.toString());
            if (clientConfigRedis.getConfiguracoes().size() == 1) {
                clientConfigRedisRepository.deleteById(id);
                return true;
            } else {
                log.info(clientConfigRedis.getConfiguracoes().toString());
                clientConfigRedis.getConfiguracoes().remove(clientConfig);
                List<Integer> codigos = new ArrayList<>();
                clientConfigRedis.getConfiguracoes()
                        .forEach(config -> config.getRegras().forEach(regra -> regra.getCodigos().forEach(codigo -> {
                            if (!codigos.contains(codigo)) {
                                codigos.add(codigo);
                            }
                        })));

                clientConfig.getRegras().forEach(regra -> regra.getCodigos().forEach(codigo -> {
                    if (!codigos.contains(codigo)) {
                        codigos.remove(codigo);
                    }
                }));

                clientConfigRedis.setCodigosImportantes(codigos);

                clientConfigRedisRepository.save(clientConfigRedis);
                return true;
            }
        } else {
            return false;
        }
    }

}
