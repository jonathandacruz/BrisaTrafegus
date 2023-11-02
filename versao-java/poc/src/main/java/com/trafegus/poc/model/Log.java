package com.trafegus.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    private UUID empresaId;

    @JsonProperty("esis_oras_codigo")
    private String placaVeiculo;
    @JsonProperty("esis_viag_codigo")
    private String codigoViagem;
    @JsonProperty("esis_espa_codigo")
    private Integer codigo;

}
