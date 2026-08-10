package services;

import domain.dto.ImagemDownloadResponse;
import domain.dto.ImagemResponse;
import domain.entity.Imagem;
import domain.entity.Individuo;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagemServiceTest {

    @InjectMocks
    ImagemService imagemService;

    @Mock
    ImagemRepository imagemRepository;

    @Mock
    IndividuoRepository individuoRepository;

    private Individuo individuoMock;
    private byte[] dadosValidos;

    @BeforeEach
    void setUp() {
        individuoMock = new Individuo();
        individuoMock.id = 1L;

        dadosValidos = new byte[]{1, 2, 3, 4, 5};
    }

    @Nested
    @DisplayName("Upload de Imagem")
    class Upload {

        @Test
        @DisplayName("Deve realizar upload com sucesso para dados válidos (JPEG, PNG, GIF, WEBP)")
        void deveRealizarUploadComSucesso() {
            when(individuoRepository.findById(1L)).thenReturn(individuoMock);
            doNothing().when(imagemRepository).persist(any(Imagem.class));

            ImagemResponse response = imagemService.upload(1L, dadosValidos, "image/jpeg", "folha.jpg");

            assertNotNull(response);
            assertEquals("folha.jpg", response.nome);
            assertEquals("image/jpeg", response.tipoMime);
            assertEquals(1L, response.individuoId);
            verify(imagemRepository, times(1)).persist(any(Imagem.class));
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando o indivíduo não for encontrado")
        void deveLancarExcecaoQuandoIndividuoNaoEncontrado() {
            when(individuoRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () ->
                    imagemService.upload(99L, dadosValidos, "image/png", "foto.png")
            );
            verify(imagemRepository, never()).persist(any(Imagem.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o arquivo for nulo ou vazio")
        void deveLancarExcecaoQuandoArquivoVazio() {
            when(individuoRepository.findById(1L)).thenReturn(individuoMock);

            assertThrows(BadRequestException.class, () ->
                    imagemService.upload(1L, new byte[0], "image/jpeg", "foto.jpg")
            );
            verify(imagemRepository, never()).persist(any(Imagem.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o arquivo exceder 10MB")
        void deveLancarExcecaoQuandoArquivoExcederTamanhoMaximo() {
            byte[] arquivoGrande = new byte[10 * 1024 * 1024 + 1]; // > 10MB
            when(individuoRepository.findById(1L)).thenReturn(individuoMock);

            assertThrows(BadRequestException.class, () ->
                    imagemService.upload(1L, arquivoGrande, "image/jpeg", "foto.jpg")
            );
            verify(imagemRepository, never()).persist(any(Imagem.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o tipo MIME for inválido")
        void deveLancarExcecaoQuandoTipoMimeInvalido() {
            when(individuoRepository.findById(1L)).thenReturn(individuoMock);

            assertThrows(BadRequestException.class, () ->
                    imagemService.upload(1L, dadosValidos, "application/pdf", "documento.pdf")
            );
            verify(imagemRepository, never()).persist(any(Imagem.class));
        }
    }

    @Nested
    @DisplayName("Obter e Download por ID")
    class ObterEDownload {

        @Test
        @DisplayName("Deve obter imagem por ID com sucesso")
        void deveObterPorIdComSucesso() {
            Imagem imagem = new Imagem("foto.jpg", dadosValidos, "image/jpeg", individuoMock);
            imagem.id = 10L;

            when(imagemRepository.findById(10L)).thenReturn(imagem);

            Imagem resultado = imagemService.obterPorId(10L);

            assertNotNull(resultado);
            assertEquals(10L, resultado.id);
            assertEquals("foto.jpg", resultado.nome);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException em obterPorId para ID inexistente")
        void deveLancarExcecaoEmObterPorIdQuandoInexistente() {
            when(imagemRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> imagemService.obterPorId(99L));
        }

        @Test
        @DisplayName("Deve realizar download com sucesso")
        void deveRealizarDownloadComSucesso() {
            Imagem imagem = new Imagem("foto.png", dadosValidos, "image/png", individuoMock);
            imagem.id = 10L;

            when(imagemRepository.findById(10L)).thenReturn(imagem);

            ImagemDownloadResponse response = imagemService.download(10L);

            assertNotNull(response);
            assertEquals("foto.png", response.nome);
            assertEquals("image/png", response.tipoMime);
            assertArrayEquals(dadosValidos, response.conteudo);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException em download para ID inexistente")
        void deveLancarExcecaoEmDownloadQuandoInexistente() {
            when(imagemRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> imagemService.download(99L));
        }
    }

    @Nested
    @DisplayName("Deletar e Listar por Indivíduo")
    class DeletarEListar {

        @Test
        @DisplayName("Deve deletar imagem com sucesso")
        void deveDeletarComSucesso() {
            Imagem imagem = new Imagem("foto.jpg", dadosValidos, "image/jpeg", individuoMock);
            imagem.id = 10L;

            when(imagemRepository.findById(10L)).thenReturn(imagem);

            imagemService.deletar(10L);

            verify(imagemRepository, times(1)).delete(imagem);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException ao tentar deletar ID inexistente")
        void deveLancarExcecaoAoDeletarInexistente() {
            when(imagemRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> imagemService.deletar(99L));
            verify(imagemRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve listar imagens por indivíduo com sucesso")
        void deveListarPorIndividuoComSucesso() {
            Imagem imagem = new Imagem("foto.jpg", dadosValidos, "image/jpeg", individuoMock);
            imagem.id = 10L;

            when(individuoRepository.findById(1L)).thenReturn(individuoMock);
            when(imagemRepository.findByIndividuo(individuoMock)).thenReturn(List.of(imagem));

            List<ImagemResponse> lista = imagemService.listarPorIndividuo(1L);

            assertNotNull(lista);
            assertEquals(1, lista.size());
            assertEquals("foto.jpg", lista.get(0).nome);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException ao listar por indivíduo inexistente")
        void deveLancarExcecaoAoListarPorIndividuoInexistente() {
            when(individuoRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> imagemService.listarPorIndividuo(99L));
            verify(imagemRepository, never()).findByIndividuo(any());
        }
    }
}