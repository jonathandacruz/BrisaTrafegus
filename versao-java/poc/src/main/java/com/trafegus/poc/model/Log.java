package com.trafegus.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    private String empresaCNPJ;

    @JsonProperty("esis_oras_codigo")
    private String placaVeiculo;
    @JsonProperty("esis_viag_codigo")
    private String codigoViagem;
    @JsonProperty("esis_espa_codigo")
    private Integer codigo;

}
