package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import domain.dto.IAChatResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rest.OpenApiClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @InjectMocks
    AIService aiService;

    @Mock
    OpenApiClient openApiClient;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    FileUpload fileUploadMock;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        aiService.iaToken = Optional.of("sk-fake-openai-token-12345");
        aiService.iaModel = "qwen/qwen3.6-27b";

        tempFile = File.createTempFile("teste_img", ".jpg");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), new byte[]{1, 2, 3, 4, 5});
    }

    @Nested
    @DisplayName("Validação do Payload de Consulta")
    class ValidacaoPayload {

        @Test
        @DisplayName("Deve lançar BadRequestException quando o payload for nulo")
        void deveLancarExcecaoQuandoPayloadNulo() {
            assertThrows(BadRequestException.class, () -> aiService.consultaEspecieIA(null));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando a lista de imagens for nula ou vazia")
        void deveLancarExcecaoQuandoImagensVazias() {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = new ArrayList<>();

            assertThrows(BadRequestException.class, () -> aiService.consultaEspecieIA(request));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o número de imagens for maior que 5")
        void deveLancarExcecaoQuandoMaisDeCincoImagens() {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = List.of(
                    fileUploadMock, fileUploadMock, fileUploadMock,
                    fileUploadMock, fileUploadMock, fileUploadMock
            );

            assertThrows(BadRequestException.class, () -> aiService.consultaEspecieIA(request));
        }

        @Test
        @DisplayName("Deve lançar InternalServerErrorException se o modelo configurado não suportar visão")
        void deveLancarExcecaoQuandoModeloNaoSuportaVisao() {
            aiService.iaModel = "llama-3.3-70b-versatile";

            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = List.of(fileUploadMock);

            assertThrows(InternalServerErrorException.class, () -> aiService.consultaEspecieIA(request));
        }
    }

    @Nested
    @DisplayName("Validação de Imagens e Configuração")
    class ValidacaoImagensEConfig {

        @Test
        @DisplayName("Deve lançar BadRequestException quando o arquivo físico da imagem for nulo")
        void deveLancarExcecaoQuandoFileUploadedNulo() {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = List.of(fileUploadMock);

            when(fileUploadMock.contentType()).thenReturn("image/jpeg");
            when(fileUploadMock.uploadedFile()).thenReturn(null);

            assertThrows(BadRequestException.class, () -> aiService.consultaEspecieIA(request));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o Content-Type não for image/*")
        void deveLancarExcecaoQuandoMimeTypeInvalido() {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = List.of(fileUploadMock);

            when(fileUploadMock.contentType()).thenReturn("application/pdf");
            when(fileUploadMock.uploadedFile()).thenReturn(tempFile.toPath());

            assertThrows(BadRequestException.class, () -> aiService.consultaEspecieIA(request));
        }

        @Test
        @DisplayName("Deve lançar InternalServerErrorException se a chave OPENAI_API_KEY não estiver configurada")
        void deveLancarExcecaoQuandoTokenAusente() {
            aiService.iaToken = Optional.empty();

            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = List.of(fileUploadMock);

            when(fileUploadMock.contentType()).thenReturn("image/jpeg");
            when(fileUploadMock.uploadedFile()).thenReturn(tempFile.toPath());

            assertThrows(InternalServerErrorException.class, () -> aiService.consultaEspecieIA(request));
        }
    }

    @Nested
    @DisplayName("Execução e Processamento da Resposta")
    class ProcessamentoResposta {

        @Test
        @DisplayName("Deve enviar requisição para IA e converter JSON válido com sucesso")
        void deveConsultarEConverterComSucesso() throws JsonProcessingException {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.nomePopular = "Ipê-Amarelo";
            request.localizacaoCidade = "Joinville";
            request.localizacaoEstado = "SC";
            request.localizacaoPais = "Brasil";
            request.bioma = "Mata Atlântica";
            request.imagensAnexo = List.of(fileUploadMock);

            when(fileUploadMock.contentType()).thenReturn("image/jpeg");
            when(fileUploadMock.uploadedFile()).thenReturn(tempFile.toPath());
            when(fileUploadMock.fileName()).thenReturn("foto.jpg");

            String jsonResposta = "{\"status_validacao\":\"Validado\",\"taxon\":{\"especie\":\"Handroanthus albus\"}}";
            IAChatResponse apiResponseMock = mock(IAChatResponse.class);
            when(apiResponseMock.getRespostaTexto()).thenReturn(jsonResposta);

            when(openApiClient.enviarPrompt(anyString(), any())).thenReturn(apiResponseMock);

            EspecieCompletaResponse esperada = new EspecieCompletaResponse();
            when(objectMapper.readValue(jsonResposta, EspecieCompletaResponse.class)).thenReturn(esperada);

            EspecieCompletaResponse resultado = aiService.consultaEspecieIA(request);

            assertNotNull(resultado);
            verify(openApiClient, times(1)).enviarPrompt(eq("Bearer sk-fake-openai-token-12345"), any());
            verify(objectMapper, times(1)).readValue(jsonResposta, EspecieCompletaResponse.class);
        }

        @Test
        @DisplayName("Deve lançar WebApplicationException (502) quando a IA retornar texto vazio")
        void deveLancarExcecaoQuandoRespostaIaVazia() {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = List.of(fileUploadMock);

            when(fileUploadMock.contentType()).thenReturn("image/jpeg");
            when(fileUploadMock.uploadedFile()).thenReturn(tempFile.toPath());

            IAChatResponse apiResponseMock = mock(IAChatResponse.class);
            when(apiResponseMock.getRespostaTexto()).thenReturn("");

            when(openApiClient.enviarPrompt(anyString(), any())).thenReturn(apiResponseMock);

            WebApplicationException ex = assertThrows(WebApplicationException.class, () -> aiService.consultaEspecieIA(request));
            assertEquals(502, ex.getResponse().getStatus());
        }

        @Test
        @DisplayName("Deve lançar WebApplicationException (502) quando a IA retornar JSON inválido")
        void deveLancarExcecaoQuandoJsonInvalido() throws JsonProcessingException {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.imagensAnexo = List.of(fileUploadMock);

            when(fileUploadMock.contentType()).thenReturn("image/jpeg");
            when(fileUploadMock.uploadedFile()).thenReturn(tempFile.toPath());

            String jsonInvalido = "{\"status\": invalid_json}";
            IAChatResponse apiResponseMock = mock(IAChatResponse.class);
            when(apiResponseMock.getRespostaTexto()).thenReturn(jsonInvalido);

            when(openApiClient.enviarPrompt(anyString(), any())).thenReturn(apiResponseMock);
            when(objectMapper.readValue(anyString(), eq(EspecieCompletaResponse.class)))
                    .thenThrow(new JsonProcessingException("Erro ao desserializar") {});

            WebApplicationException ex = assertThrows(WebApplicationException.class, () -> aiService.consultaEspecieIA(request));
            assertEquals(502, ex.getResponse().getStatus());
        }
    }
}