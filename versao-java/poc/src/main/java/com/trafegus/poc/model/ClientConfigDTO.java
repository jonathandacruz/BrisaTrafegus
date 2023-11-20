package com.trafegus.poc.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ClientConfigDTO {

    private UUID empresaId;
    private String empresaCNPJ;
    private String tipo;
    private List<Regras> regras;

}
