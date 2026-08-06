package services;

import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import domain.entity.Imagem;
import domain.entity.Individuo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class IndividuoIAService {
    private static final Logger LOG = Logger.getLogger(IndividuoIAService.class);

    @Inject
    IndividuoRepository individuoRepository;

    @Inject
    ImagemRepository imagemRepository;

    @Inject
    AIService aiService;

    @Transactional
    public EspecieCompletaResponse completarDadosComIA(Long individuoId) {
        LOG.infof("Completando dados de indivíduo via IA: %d", individuoId);

        Individuo individuo = individuoRepository.findById(individuoId);
        if (individuo == null) {
            throw new NotFoundException("Indivíduo não encontrado");
        }

        List<Imagem> imagens = imagemRepository.findByIndividuo(individuo);
        if (imagens.isEmpty()) {
            throw new BadRequestException("Nenhuma imagem encontrada para este indivíduo");
        }

        ConsultaEspecieRequest consultaRequest = construirConsultaIA(individuo, imagens);
        EspecieCompletaResponse resposta = aiService.consultaEspecieIA(consultaRequest);

        atualizarIndividuoComDadosIA(individuo, resposta);

        return resposta;
    }

    private ConsultaEspecieRequest construirConsultaIA(Individuo individuo, List<Imagem> imagens) {
        ConsultaEspecieRequest request = new ConsultaEspecieRequest();
        
        request.nomePopular = individuo.nomePopular;
        request.localizacaoCidade = individuo.levantamento.cidade;
        request.localizacaoEstado = individuo.levantamento.estado;
        request.localizacaoPais = individuo.levantamento.pais;
        request.bioma = individuo.levantamento.bioma;

        request.imagensAnexo = new ArrayList<>();
        for (Imagem img : imagens) {
            request.imagensAnexo.add(converterImagemParaFileUpload(img));
        }

        return request;
    }

    private FileUpload converterImagemParaFileUpload(Imagem imagem) {
        // Cria um FileUpload simples a partir dos dados do BLOB gravando um arquivo temporário
        try {
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("imagem_" + imagem.id, getExtensao(imagem.tipoMime));
            Files.write(tempFile, imagem.conteudo);
            tempFile.toFile().deleteOnExit();
            final java.nio.file.Path uploaded = tempFile;
            return new FileUpload() {
                @Override
                public String name() {
                    return "imagem_" + imagem.id + "." + getExtensao(imagem.tipoMime);
                }

                @Override
                public Path filePath() {
                    return tempFile;
                }

                @Override
                public String fileName() {
                    return imagem.nome;
                }

                @Override
                public String contentType() {
                    return imagem.tipoMime;
                }

                @Override
                public String charSet() {
                    return "";
                }

                @Override
                public long size() {
                    return imagem.conteudo == null ? 0L : imagem.conteudo.length;
                }

                @Override
                public java.nio.file.Path uploadedFile() {
                    return uploaded;
                }

                @Override
                public MultivaluedMap<String, String> getHeaders() {
                    return null;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar arquivo temporário", e);
        }
    }

    private String getExtensao(String tipoMime) {
        if (tipoMime == null) {
            return ".jpg";
        }
        String t = tipoMime.toLowerCase();
        if (t.contains("jpeg") || t.contains("jpg")) return ".jpg";
        if (t.contains("png")) return ".png";
        if (t.contains("gif")) return ".gif";
        if (t.contains("webp")) return ".webp";
        return ".jpg";
    }

    @Transactional
    public void atualizarIndividuoComDadosIA(Individuo individuo,
                                               EspecieCompletaResponse resposta) {
        if (resposta == null || resposta.getTaxon() == null) {
            LOG.warnf("Resposta da IA inválida para indivíduo %d", individuo.id);
            return;
        }

        EspecieCompletaResponse.TaxonDTO taxon = resposta.getTaxon();

        if (taxon.getNomeComumConfirmado() != null && !taxon.getNomeComumConfirmado().isBlank()) {
            individuo.nomePopular = taxon.getNomeComumConfirmado();
        }

        if (taxon.getEspecie() != null && !taxon.getEspecie().isBlank()) {
            individuo.nomeCientifico = taxon.getEspecie();
        }

        individuoRepository.persist(individuo);
        LOG.infof("Indivíduo %d atualizado com dados da IA", individuo.id);
    }
}
