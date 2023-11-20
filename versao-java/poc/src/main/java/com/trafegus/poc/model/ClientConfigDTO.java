package com.trafegus.poc.model;

import lombok.Data;

import java.util.List;

@Data
public class ClientConfigDTO {

    private String empresaCNPJ;
    private String tipo;
    private List<Regras> regras;

}
