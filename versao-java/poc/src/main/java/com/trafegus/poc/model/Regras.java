package com.trafegus.poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Regras {

    private String porcentagem;
    private List<Integer> codigos;

}
