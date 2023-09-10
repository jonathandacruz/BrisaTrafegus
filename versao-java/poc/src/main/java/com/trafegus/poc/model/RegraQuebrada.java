package com.trafegus.poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegraQuebrada {

    private UUID regraQuebradaId;
    private List<Integer> codigosRegrasQuebradas;
    private Integer riscoRegrasQuebradas;

}
