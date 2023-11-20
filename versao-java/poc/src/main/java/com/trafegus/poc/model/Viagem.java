package com.trafegus.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private TipoViagemEnum tipoViagem;
    private List<ProdutoViagemEnum> produtoViagem;
    private String empresaCNPJ;
    @JsonProperty("esis_oras_codigo")
    private String placaVeiculo;
    @JsonProperty("esis_viag_codigo")
    private String codigoViagem;
    private Integer riscoAtualPorcentagem;
    private String riscoAtualTipoSinistro;
    private Integer predicaoMachineLearning;
    private boolean sinistro;
    private LocalDateTime ultimaRegraQuebrada;
    private LocalDateTime inicioViagem;
    private LocalDateTime fimViagem;
    private List<RegraQuebrada> regrasQuebradas;
    private List<Integer> codigosQuebrados;
}
