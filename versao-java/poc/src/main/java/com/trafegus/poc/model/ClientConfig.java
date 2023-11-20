package com.trafegus.poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("ClientConfig")
@Document(collection = "client-config")
public class ClientConfig {

    @Id
    private UUID id;
    private String empresaCNPJ;
    private String tipo;
    private List<Regras> regras;

}
