package com.trafegus.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ViagemDTO {

    private UUID empresaId;
    private String empresaCNPJ;
    @JsonProperty("esis_oras_codigo")
    private String placaVeiculo;
    @JsonProperty("esis_viag_codigo")
    private String codigoViagem;
    private Integer riscoAtualPorcentagem;
    private LocalDateTime inicioViagem;

}
