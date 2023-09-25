package com.trafegus.poc.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("config")
public class ClientConfigRedis {

    @Id
    private UUID id;
    private List<ClientConfig> configuracoes;
    private List<Integer> codigosImportantes;

}
