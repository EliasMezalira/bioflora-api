package controller;

import domain.dto.ImagemDownloadResponse;
import domain.dto.ImagemResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import services.ImagemService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ImagemControllerTest {

    @Inject
    ImagemController imagemController;

    @InjectMock
    ImagemService imagemService;

    @Nested
    @DisplayName("Caminho feliz")
    class HappyPath {

        @Test
        @DisplayName("Deve enviar arquivo e retornar resposta de upload")
        void deveEnviarArquivoERetornarRespostaDeUpload() throws IOException {
            FileUpload arquivo = mock(FileUpload.class);
            Path tempFile = Files.createTempFile("upload-test", ".jpg");
            try {
                Files.write(tempFile, new byte[]{1, 2, 3, 4});
                when(arquivo.uploadedFile()).thenReturn(tempFile);
                when(arquivo.filePath()).thenReturn(tempFile);
                when(arquivo.contentType()).thenReturn("image/png");
                when(arquivo.fileName()).thenReturn("foto.png");

                ImagemController.UploadForm form = new ImagemController.UploadForm();
                form.arquivo = arquivo;

                ImagemResponse expectedResponse = new ImagemResponse();
                when(imagemService.upload(eq(1L), any(), eq("image/png"), eq("foto.png")))
                        .thenReturn(expectedResponse);

                ImagemResponse actual = imagemController.upload(1L, form);

                assertNotNull(actual);
                assertSame(expectedResponse, actual);
                verify(imagemService, times(1))
                        .upload(eq(1L), any(), eq("image/png"), eq("foto.png"));
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }

        @Test
        @DisplayName("Deve retornar response correto ao baixar imagem")
        void deveRetornarResponseCorretoAoBaixarImagem() {
            ImagemDownloadResponse responseDto = new ImagemDownloadResponse();
            responseDto.conteudo = new byte[]{10, 20, 30};
            responseDto.nome = "foto.jpg";
            responseDto.tipoMime = "image/jpeg";

            when(imagemService.download(1L)).thenReturn(responseDto);

            Response response = imagemController.download(1L);

            assertEquals(200, response.getStatus());
            assertArrayEquals(responseDto.conteudo, (byte[]) response.getEntity());
            assertEquals("inline; filename=\"foto.jpg\"", response.getHeaderString("Content-Disposition"));
            assertEquals("image/jpeg", response.getMediaType().toString());
        }

        @Test
        @DisplayName("Deve listar imagens do indivíduo")
        void deveListarImagensDoIndividuo() {
            List<ImagemResponse> expected = List.of(new ImagemResponse());
            when(imagemService.listarPorIndividuo(2L)).thenReturn(expected);

            List<ImagemResponse> actual = imagemController.listarPorIndividuo(2L);

            assertNotNull(actual);
            assertEquals(expected, actual);
            verify(imagemService, times(1)).listarPorIndividuo(2L);
        }

        @Test
        @DisplayName("Deve deletar imagem chamando serviço")
        void deveDeletarImagemChamandoServico() {
            imagemController.deletar(3L);

            verify(imagemService, times(1)).deletar(3L);
        }
    }

    @Nested
    @DisplayName("Fluxos de erro")
    class ErrorFlows {

        @Test
        @DisplayName("Deve propagar RuntimeException quando serviço falhar no upload")
        void devePropagarRuntimeExceptionQuandoServicoFalharNoUpload() throws IOException {
            FileUpload arquivo = mock(FileUpload.class);
            Path tempFile = Files.createTempFile("upload-fail-test", ".jpg");
            try {
                Files.write(tempFile, new byte[]{1, 2});
                when(arquivo.uploadedFile()).thenReturn(tempFile);
                when(arquivo.filePath()).thenReturn(tempFile);
                when(arquivo.contentType()).thenReturn("image/png");
                when(arquivo.fileName()).thenReturn("falha.png");

                ImagemController.UploadForm form = new ImagemController.UploadForm();
                form.arquivo = arquivo;

                when(imagemService.upload(eq(1L), any(), eq("image/png"), eq("falha.png")))
                        .thenThrow(new RuntimeException("Serviço indisponível"));

                RuntimeException exception = assertThrows(RuntimeException.class,
                        () -> imagemController.upload(1L, form));

                assertEquals("Serviço indisponível", exception.getMessage());
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }

        @Test
        @DisplayName("Deve lançar IOException quando arquivo não puder ser lido")
        void deveLancarIOExceptionQuandoArquivoNaoPuderSerLido() {
            FileUpload arquivo = mock(FileUpload.class);
            Path nonexistentPath = Path.of("nao-existe.jpg");
            when(arquivo.uploadedFile()).thenReturn(nonexistentPath);
            when(arquivo.filePath()).thenReturn(nonexistentPath);
            when(arquivo.contentType()).thenReturn("image/png");
            when(arquivo.fileName()).thenReturn("nao-existe.jpg");

            ImagemController.UploadForm form = new ImagemController.UploadForm();
            form.arquivo = arquivo;

            assertThrows(IOException.class, () -> imagemController.upload(1L, form));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando formulário for nulo")
        void deveLancarNullPointerExceptionQuandoFormularioForNulo() {
            assertThrows(NullPointerException.class,
                    () -> imagemController.upload(1L, null));
        }

        @Test
        @DisplayName("Deve propagar RuntimeException quando serviço falhar na exclusão")
        void devePropagarRuntimeExceptionQuandoServicoFalharNaExclusao() {
            doThrow(new RuntimeException("Falha ao deletar")).when(imagemService).deletar(5L);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> imagemController.deletar(5L));

            assertEquals("Falha ao deletar", exception.getMessage());
        }
    }
}
