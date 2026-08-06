package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.dto.ChatMessage;
import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import domain.dto.IAChatRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import rest.OpenApiClient;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

@ApplicationScoped
public class AIService {
    private static final Logger LOG = Logger.getLogger(AIService.class);
    private static final int MAX_IMAGENS = 5;
    private static final long MAX_IMAGE_BYTES_BASE64_GROQ = 3L * 1024L * 1024L;

    @Inject
    @RestClient
    OpenApiClient openApiClient;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "ai.openai.token")
    Optional<String> iaToken;

    @ConfigProperty(name = "ai.openai.model", defaultValue = "qwen/qwen3.6-27b")
    String iaModel;

    private static final String PROMPT_TEMPLATE = """
            Você está sendo consultado para identificar e validar uma espécie de planta observada em um levantamento de flora.
            Com base nas imagens da planta e nos dados de localização fornecidos, identifique a espécie mais provável.
            
            IMPORTANTE: Priorize dados que conferem com a localização geográfica e bioma informado.
            Se não tiver certeza absoluta, indique a espécie mais provável baseado nas características visuais.
            
            Retorne exclusivamente um JSON válido, sem markdown, exatamente no formato:
            {
              "status_validacao": "Validado com sucesso",
              "taxon": {
                "reino": "Plantae",
                "divisao": "Magnoliophyta",
                "familia": "Bignoniaceae",
                "genero": "Handroanthus",
                "especie": "Handroanthus albus",
                "autor": "(Cham.) Mattos",
                "nome_comum_confirmado": "Ipê-amarelo"
              },
              "geografia": {
                "municipio": "Joinville",
                "uf": "SC",
                "bioma": "Mata Atlântica"
              },
              "ecologia": {
                "status_conservacao": "Pouco preocupante (LC)",
                "origem": "Nativa",
                "importancia_ecologica": "Espécie importante para o ecossistema local."
              }
            }

            DADOS DO LEVANTAMENTO:
            %s
            
            Analise as imagens e os dados acima, e retorne APENAS o JSON no formato especificado.
            """;

    public EspecieCompletaResponse consultaEspecieIA(ConsultaEspecieRequest dadosConsulta) {
        validarPayload(dadosConsulta);

        LOG.infof(
                "Iniciando consulta de especie via IA. nomePopular=%s, cidade=%s, estado=%s, pais=%s, bioma=%s, quantidadeImagens=%d",
                valorParaLog(dadosConsulta.nomePopular),
                valorParaLog(dadosConsulta.localizacaoCidade),
                valorParaLog(dadosConsulta.localizacaoEstado),
                valorParaLog(dadosConsulta.localizacaoPais),
                valorParaLog(dadosConsulta.bioma),
                dadosConsulta.imagensAnexo.size()
        );

        List<IAChatRequest.ContentPart> contentParts = new ArrayList<>();
        contentParts.add(IAChatRequest.ContentPart.text(String.format(PROMPT_TEMPLATE, montarDadosEntrada(dadosConsulta))));

        for (int i = 0; i < dadosConsulta.imagensAnexo.size(); i++) {
            FileUpload imagem = dadosConsulta.imagensAnexo.get(i);
            String mimeType = obterMimeType(imagem);
            validarImagem(imagem, mimeType, i + 1);
            try {
                byte[] imageBytes = Files.readAllBytes(imagem.uploadedFile());
                // reduzir imagem se necessário para caber no limite do provider
                imageBytes = reduzirImagemParaIA(imageBytes, mimeType, i + 1);
                validarTamanhoImagem(imageBytes, i + 1);
                LOG.infof(
                        "Imagem %d carregada para consulta IA. fileName=%s, contentType=%s, tamanhoBytes=%d",
                        i + 1,
                        valorParaLog(imagem.fileName()),
                        valorParaLog(mimeType),
                        imageBytes.length
                );

                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                contentParts.add(IAChatRequest.ContentPart.image(base64Image, mimeType));
            } catch (IOException e) {
                LOG.errorf(e, "Nao foi possivel ler a imagem %d enviada para consulta IA.", i + 1);
                throw new BadRequestException("Não foi possível ler uma das imagens enviadas.", e);
            }
        }

        try {
            LOG.infof("Enviando consulta para IA. model=%s, partesConteudo=%d", iaModel, contentParts.size());
            var response = openApiClient.enviarPrompt(
                "Bearer " + tokenConfigurado(),
                new IAChatRequest(
                        iaModel,
                        "minimal",
                        List.of(
                                new ChatMessage("system", "Você atua como biólogo especialista em flora e identificação de espécies. Responda sempre apenas com JSON válido no formato solicitado."),
                                new ChatMessage("user", contentParts)
                        ),
                        0.2,
                        IAChatRequest.ResponseFormat.jsonObject()
                )
        );

            String respostaTexto = response == null ? null : response.getRespostaTexto();
            LOG.infof(
                    "Resposta recebida da IA. respostaNula=%s, tamanhoResposta=%d",
                    respostaTexto == null,
                    respostaTexto == null ? 0 : respostaTexto.length()
            );
            return converterResposta(respostaTexto);
        } catch (WebApplicationException e) {
            LOG.errorf(e, "Falha HTTP ao consultar/processar resposta da IA. status=%d", e.getResponse() == null ? -1 : e.getResponse().getStatus());
            throw e;
        } catch (RuntimeException e) {
            LOG.error("Erro inesperado ao consultar IA.", e);
            throw e;
        }
    }

    private void validarPayload(ConsultaEspecieRequest dadosConsulta) {
        if (dadosConsulta == null) {
            throw new BadRequestException("Payload obrigatório.");
        }
        if (dadosConsulta.imagensAnexo == null || dadosConsulta.imagensAnexo.isEmpty()) {
            throw new BadRequestException("Envie ao menos uma imagem no campo imagensAnexo.");
        }
        if (dadosConsulta.imagensAnexo.size() > MAX_IMAGENS) {
            throw new BadRequestException("Envie no maximo " + MAX_IMAGENS + " imagens no campo imagensAnexo.");
        }
        if (modeloSemSuporteAImagem(iaModel)) {
            throw new InternalServerErrorException(
                    "Modelo configurado nao aceita imagens em messages[].content. Configure GROQ_MODEL para um modelo vision com JSON mode, por exemplo qwen/qwen3.6-27b."
            );
        }
    }

    private String montarDadosEntrada(ConsultaEspecieRequest dadosConsulta) {
        StringJoiner dados = new StringJoiner("\n");
        adicionarSePreenchido(dados, "Nome popular", dadosConsulta.nomePopular);
        adicionarSePreenchido(dados, "Cidade", dadosConsulta.localizacaoCidade);
        adicionarSePreenchido(dados, "Estado", dadosConsulta.localizacaoEstado);
        adicionarSePreenchido(dados, "País", dadosConsulta.localizacaoPais);
        adicionarSePreenchido(dados, "Bioma", dadosConsulta.bioma);
        return dados.length() == 0 ? "Nenhuma informação textual adicional." : dados.toString();
    }

    private void adicionarSePreenchido(StringJoiner dados, String rotulo, String valor) {
        if (valor != null && !valor.isBlank()) {
            dados.add(rotulo + ": " + valor.trim());
        }
    }

    private String obterMimeType(FileUpload imagem) {
        if (imagem == null) {
            return null;
        }
        return imagem.contentType() == null || imagem.contentType().isBlank()
                ? "image/jpeg"
                : imagem.contentType();
    }

    private void validarImagem(FileUpload imagem, String mimeType, int indice) {
        if (imagem == null || imagem.uploadedFile() == null) {
            throw new BadRequestException("Imagem " + indice + " invalida no campo imagensAnexo.");
        }
        if (mimeType == null || !mimeType.toLowerCase().startsWith("image/")) {
            throw new BadRequestException("Imagem " + indice + " deve possuir Content-Type image/*.");
        }
    }

    private void validarTamanhoImagem(byte[] imageBytes, int indice) {
        if (imageBytes.length == 0) {
            throw new BadRequestException("Imagem " + indice + " esta vazia.");
        }
        if (imageBytes.length > MAX_IMAGE_BYTES_BASE64_GROQ) {
            throw new BadRequestException("Imagem " + indice + " excede o tamanho suportado para envio em base64.");
        }
    }

    private boolean modeloSemSuporteAImagem(String model) {
        if (model == null || model.isBlank()) {
            return true;
        }
        String normalizedModel = model.trim().toLowerCase();
        return normalizedModel.equals("llama-3.3-70b-versatile")
                || normalizedModel.equals("llama-3.1-8b-instant")
                || normalizedModel.startsWith("openai/gpt-oss");
    }

    private String tokenConfigurado() {
        if (iaToken.isEmpty() || iaToken.get().isBlank()) {
            LOG.error("Token da OpenAI nao configurado. Defina a variavel/configuracao ai.openai.token.");
        }
        return iaToken
                .filter(token -> !token.isBlank())
                .orElseThrow(() -> new InternalServerErrorException("Configure a variável OPENAI_API_KEY para consultar a IA."));
    }

    private EspecieCompletaResponse converterResposta(String respostaTexto) {
        if (respostaTexto == null || respostaTexto.isBlank()) {
            LOG.error("A IA retornou uma resposta vazia.");
            throw new WebApplicationException("A IA retornou uma resposta vazia.", 502);
        }

        try {
            return objectMapper.readValue(extrairJson(respostaTexto), EspecieCompletaResponse.class);
        } catch (JsonProcessingException e) {
            LOG.errorf(e, "A IA retornou um JSON invalido para o contrato esperado. resposta=%s", respostaTexto);
            throw new WebApplicationException("A IA retornou um JSON inválido para o contrato esperado.", e, 502);
        }
    }

    private String extrairJson(String respostaTexto) {
        String texto = respostaTexto.trim();
        int inicio = texto.indexOf('{');
        int fim = texto.lastIndexOf('}');
        if (inicio < 0 || fim <= inicio) {
            return texto;
        }
        return texto.substring(inicio, fim + 1);
    }

    private String valorParaLog(String valor) {
        return valor == null || valor.isBlank() ? "<vazio>" : valor.trim();
    }

    /**
     * Tenta reduzir a imagem convertendo para JPEG, reduzindo qualidade e escala até caber no limite.
     */
    private byte[] reduzirImagemParaIA(byte[] original, String mimeType, int indice) {
        if (original == null) return original;
        if (original.length <= MAX_IMAGE_BYTES_BASE64_GROQ) return original;
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(original));
            if (img == null) {
                LOG.warnf("Imagem %d tem formato não suportado para redução; mantendo original (tamanho=%d)", indice, original.length);
                return original;
            }

            float quality = 0.85f;
            double scale = 1.0;

            for (int attempt = 0; attempt < 8; attempt++) {
                int targetW = Math.max(1, (int) (img.getWidth() * scale));
                int targetH = Math.max(1, (int) (img.getHeight() * scale));

                BufferedImage converted = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = converted.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(img, 0, 0, targetW, targetH, null);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                if (!writers.hasNext()) {
                    ImageIO.write(converted, "jpg", baos);
                } else {
                    ImageWriter writer = writers.next();
                    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                    writer.setOutput(ios);
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    if (param.canWriteCompressed()) {
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(Math.max(0.25f, quality));
                    }
                    writer.write(null, new javax.imageio.IIOImage(converted, null, null), param);
                    ios.close();
                    writer.dispose();
                }

                byte[] out = baos.toByteArray();
                if (out.length <= MAX_IMAGE_BYTES_BASE64_GROQ) {
                    LOG.infof("Imagem %d reduzida para %d bytes (quality=%.2f, scale=%.2f)", indice, out.length, quality, scale);
                    return out;
                }

                // reduzir qualidade até um limite, depois reduzir escala
                if (quality > 0.5f) {
                    quality -= 0.15f;
                } else {
                    scale *= 0.8;
                }
            }

            LOG.warnf("Não foi possível reduzir imagem %d para o tamanho desejado; tamanho atual=%d", indice, original.length);
            return original;
        } catch (IOException e) {
            LOG.warnf(e, "Falha ao processar imagem %d para redução; usando original.", indice);
            return original;
        }
    }
}
