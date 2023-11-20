package com.trafegus.poc.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("config")
public class ClientConfigRedis {

    @Id
    private String id;
    private List<ClientConfig> configuracoes;
    private List<Integer> codigosImportantes;

}
