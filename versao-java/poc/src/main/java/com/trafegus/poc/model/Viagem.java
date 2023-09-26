package com.trafegus.poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("Viagem")
@Document(collection = "viagens")
public class Viagem {

    @Id
    private UUID id;
    private UUID empresaId;
    private UUID caminhao;
    private UUID rota;
    private UUID motorista;
    private Integer riscoAtualPorcentagem;
    private String riscoAtualTipoSinistro;
    private Integer predicaoMachineLearning;
    private boolean sinistro;
    private LocalDateTime ultimaRegraQuebrada;
    private LocalDateTime inicioViagem;
    private LocalDateTime fimViagem;
    private List<RegraQuebrada> regrasQuebradas;
}
