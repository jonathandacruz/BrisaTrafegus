package com.trafegus.poc.services.poc;

import com.trafegus.poc.model.ClientConfig;
import com.trafegus.poc.model.ClientConfigRedis;
import com.trafegus.poc.model.Log;
import com.trafegus.poc.model.RegraQuebrada;
import com.trafegus.poc.model.Regras;
import com.trafegus.poc.model.Viagem;
import com.trafegus.poc.repository.ClientConfigRedisRepository;
import com.trafegus.poc.repository.ClientConfigRepository;
import com.trafegus.poc.repository.ViagemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PocServiceImpl implements PocService {

    @Autowired
    private ClientConfigRepository clientConfigRepository;

    @Autowired
    private ClientConfigRedisRepository clientConfigRedisRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    public Boolean processLog(Log logRecebido) {
        Optional<ClientConfigRedis> clientConfigRedis = getClientConfig(logRecebido.getEmpresaCNPJ());

        if (clientConfigRedis.isPresent()) {
            ClientConfigRedis presentClientConfigRedis = clientConfigRedis.get();
            if (isLogImportante(logRecebido, presentClientConfigRedis)) {
                this.processLogImportante(logRecebido);
                return true;
            } else {
                log.info("Log não importante recebido, ignorando... = [{}]", logRecebido);
                return false;
            }
        } else {
            log.info("Nenhuma configuração cadastrada! Nenhum dado encontrado no Redis.");
            return false;
        }
    }

    private Optional<ClientConfigRedis> getClientConfig(String empresaCNPJ) {
        return clientConfigRedisRepository.findById(empresaCNPJ);
    }

    private boolean isLogImportante(Log logRecebido, ClientConfigRedis clientConfigRedis) {
        return clientConfigRedis.getCodigosImportantes().contains(logRecebido.getCodigo());
    }

    private void processLogImportante(Log logRecebido) {
        List<ClientConfig> clientConfigs = getClientConfigs(logRecebido.getCodigo(), logRecebido.getEmpresaCNPJ());
        Optional<Viagem> viagemOptional = viagemRepository.findByCodigoViagemAndEmpresaCNPJ(logRecebido.getCodigoViagem(), logRecebido.getEmpresaCNPJ());

        if (viagemOptional.isPresent() && !clientConfigs.isEmpty()) {
            Viagem viagem = viagemOptional.get();
            this.updateViagemWithBrokenRules(logRecebido, clientConfigs, viagem);
            viagemRepository.save(viagem);
        } else {
            log.info("Viagem não encontrada ou lista de configurações vazia");
        }
    }

    private List<ClientConfig> getClientConfigs(Integer codigo, String empresaCNPJ) {
        return clientConfigRepository.findClientConfigByRegrasContainingAndEmpresaCNPJ(codigo, empresaCNPJ);
    }

    private void updateViagemWithBrokenRules(Log logRecebido, List<ClientConfig> clientConfigs, Viagem viagem) {
        List<RegraQuebrada> regrasQuebradas = new ArrayList<>();

        clientConfigs.forEach(config -> {
            RegraQuebrada regraQuebrada = findBrokenRule(logRecebido, config, viagem);
            if (regraQuebrada != null) {
                regrasQuebradas.add(regraQuebrada);
                log.info("Regra quebrada adicionada = [{}]", regraQuebrada);
                viagem.setUltimaRegraQuebrada(LocalDateTime.now());
            }
        });

        viagem.getRegrasQuebradas().addAll(regrasQuebradas);

        this.updateViagemRisk(viagem);
        log.info("Viagem atualizada = [{}]", viagem);
    }

    private RegraQuebrada findBrokenRule(Log logRecebido, ClientConfig config, Viagem viagem) {
        RegraQuebrada regraQuebrada = null;
        for (Regras regra : config.getRegras()) {
            if (regra.getCodigos().size() == 1) {
                if (regra.getCodigos().get(0).equals(logRecebido.getCodigo())) {
                    regraQuebrada = createRegraQuebrada(logRecebido, config, regra);
                    log.info("Regra quebrada encontrada = [{}]", regra);
                    break;
                }
            } else {
                if (regra.getCodigos().contains(logRecebido.getCodigo())) {
                    viagem.addCodigoRecebido(logRecebido.getCodigo());
                    if (this.verificarSeTodosOsCodigosDaRegraForamQuebrados(viagem, regra)) {
                        regraQuebrada = createRegraQuebrada(logRecebido, config, regra);
                        log.info("Regra quebrada encontrada = [{}]", regra);
                        viagem.setCodigosQuebrados(new HashSet<>());
                        break;
                    } else {
                        log.info("Regra encontrada não teve todos os códigos quebrados = [{}]", regra);
                    }
                }
            }

        }
        return regraQuebrada;
    }

    private RegraQuebrada createRegraQuebrada(Log logRecebido, ClientConfig config, Regras regra) {
        RegraQuebrada regraQuebrada = new RegraQuebrada(null, null, null, new ArrayList<>(), null);
        regraQuebrada.setRegraQuebradaId(config.getId());
        regraQuebrada.setTipoRegra(config.getTipo());
        regraQuebrada.setDataHoraRegraQuebrada(LocalDateTime.now());
        regraQuebrada.getCodigosRegrasQuebradas().add(logRecebido.getCodigo());
        regraQuebrada.setRiscoRegrasQuebradas(Integer.valueOf(regra.getPorcentagem()));
        return regraQuebrada;
    }

    private void updateViagemRisk(Viagem viagem) {
        viagem.getRegrasQuebradas().forEach(regraQuebrada -> {
            if (regraQuebrada.getRiscoRegrasQuebradas() > viagem.getRiscoAtualPorcentagem()) {
                viagem.setRiscoAtualPorcentagem(regraQuebrada.getRiscoRegrasQuebradas());
                viagem.setRiscoAtualTipoSinistro(regraQuebrada.getTipoRegra());
            }
        });
    }

    private Boolean verificarSeTodosOsCodigosDaRegraForamQuebrados (Viagem viagem, Regras regra) {
        return viagem.getCodigosQuebrados().containsAll(regra.getCodigos());
    }


}
