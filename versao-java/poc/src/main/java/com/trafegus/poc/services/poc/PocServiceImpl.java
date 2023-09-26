package com.trafegus.poc.services.poc;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.model.Log;
import com.trafegus.poc.model.RegraQuebrada;
import com.trafegus.poc.model.Viagem;
import com.trafegus.poc.repository.ClientConfigRedisRepository;
import com.trafegus.poc.repository.ClientConfigRepository;
import com.trafegus.poc.repository.ViagemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PocServiceImpl implements PocService{

    @Autowired
    private ClientConfigRepository clientConfigRepository;

    @Autowired
    private ClientConfigRedisRepository clientConfigRedisRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Override
    public Boolean processLog(Log logRecebido) {

        Optional<ClientConfigRedis> clientConfigRedis =
                this.clientConfigRedisRepository.findById(logRecebido.getEmpresaId());

        if (clientConfigRedis.isPresent()) {
            ClientConfigRedis presentClientConfigRedis = clientConfigRedis.get();
            if (presentClientConfigRedis.getCodigosImportantes().contains(logRecebido.getCodigo())) {
                log.info("Log que deveria ser processado recebido = [{}]", logRecebido);

                List<ClientConfig> clientConfigs =
                        this.clientConfigRepository.findClientConfigByRegrasContaining(logRecebido.getCodigo());
                Optional<Viagem> viagemOptional = viagemRepository.findById(logRecebido.getViagemId());

                if (viagemOptional.isPresent() && !clientConfigs.isEmpty()) {
                    log.info("Viagem encontrada, verificando regra quebrada...");
                    Viagem viagem = viagemOptional.get();
                    viagem.setUltimaRegraQuebrada(LocalDateTime.now());
                    List<RegraQuebrada> regrasQuebradas = new ArrayList<>();
                    clientConfigs.forEach(config -> {
                        RegraQuebrada regraQuebrada = new RegraQuebrada(null, null, null, new ArrayList<>(), null);
                        config.getRegras().forEach(regra -> {
                            if (regra.getCodigos().contains(logRecebido.getCodigo())) {
                                log.info("Regra quebrada encontrada = [{}]", regra);
                                regraQuebrada.setRegraQuebradaId(config.getId());
                                regraQuebrada.setTipoRegra(config.getTipo());
                                regraQuebrada.setDataHoraRegraQuebrada(LocalDateTime.now());
                                regraQuebrada.getCodigosRegrasQuebradas().add(logRecebido.getCodigo());
                                regraQuebrada.setRiscoRegrasQuebradas(
                                        Integer.valueOf(regra.getPorcentagem()));
                            }
                        });
                        regrasQuebradas.add(regraQuebrada);
                        log.info("Regra quebrada adicionada = [{}]", regraQuebrada);
                    });

                    viagem.getRegrasQuebradas().addAll(regrasQuebradas);

                    viagem.getRegrasQuebradas().forEach(regraQuebrada -> {
                        if (regraQuebrada.getRiscoRegrasQuebradas() > viagem.getRiscoAtualPorcentagem()) {
                            viagem.setRiscoAtualPorcentagem(regraQuebrada.getRiscoRegrasQuebradas());
                            viagem.setRiscoAtualTipoSinistro(regraQuebrada.getTipoRegra());
                        }
                    });
                    log.info("Viagem atualizada = [{}]", viagem);
                    this.viagemRepository.save(viagem);
                    return true;
                } else {
                    log.info("Viagem nao encontrada ou lista de configurações vazia");
                    return false;
                }
            } else {
                log.info("Log não importante recebido, ignorando... = [{}]", logRecebido);
                return false;
            }
        } else {
            log.info("Nenhuma configuração cadastrada! Nenhum dado encontrado no Redis.");
            return false;
        }
    }
}
