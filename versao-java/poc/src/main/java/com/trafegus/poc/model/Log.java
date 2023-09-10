package com.trafegus.poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    private UUID empresaId;
    private UUID viagemId;
    private UUID motoristaId;
    private Integer codigo;

}
