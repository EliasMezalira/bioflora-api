package controller;

import domain.dto.IndividuoCreateRequest;
import domain.dto.IndividuoResponse;
import domain.dto.PageResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import services.IndividuoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class IndividuoControllerTest {

    @Inject
    IndividuoController individuoController;

    @InjectMock
    IndividuoService individuoService;

    @Nested
    @DisplayName("Caminho feliz")
    class HappyPath {

        @Test
        @DisplayName("Deve criar indivíduo delegando para o serviço")
        void deveCriarIndividuoDelegandoParaServico() {
            IndividuoCreateRequest request = new IndividuoCreateRequest();
            IndividuoResponse expected = new IndividuoResponse();

            when(individuoService.criar(10L, request)).thenReturn(expected);

            IndividuoResponse actual = individuoController.criar(10L, request);

            assertSame(expected, actual);
            verify(individuoService, times(1)).criar(10L, request);
        }

        @Test
        @DisplayName("Deve obter indivíduo por id delegando para o serviço")
        void deveObterIndividuoPorIdDelegandoParaServico() {
            IndividuoResponse expected = new IndividuoResponse();

            when(individuoService.obterPorId(5L)).thenReturn(expected);

            IndividuoResponse actual = individuoController.obterPorId(5L);

            assertSame(expected, actual);
            verify(individuoService, times(1)).obterPorId(5L);
        }

        @Test
        @DisplayName("Deve atualizar indivíduo delegando para o serviço")
        void deveAtualizarIndividuoDelegandoParaServico() {
            IndividuoCreateRequest request = new IndividuoCreateRequest();
            IndividuoResponse expected = new IndividuoResponse();

            when(individuoService.atualizar(7L, request)).thenReturn(expected);

            IndividuoResponse actual = individuoController.atualizar(7L, request);

            assertSame(expected, actual);
            verify(individuoService, times(1)).atualizar(7L, request);
        }

        @Test
        @DisplayName("Deve deletar indivíduo delegando para o serviço")
        void deveDeletarIndividuoDelegandoParaServico() {
            individuoController.deletar(3L);

            verify(individuoService, times(1)).deletar(3L);
        }

        @Test
        @DisplayName("Deve listar indivíduos com paginação")
        void deveListarIndividuosComPaginacao() {
            PageResponse<IndividuoResponse> expectedPage = mock(PageResponse.class);

            when(individuoService.listar(0, 10)).thenReturn(expectedPage);

            PageResponse<IndividuoResponse> actual = individuoController.listar(0, 10);

            assertSame(expectedPage, actual);
            verify(individuoService, times(1)).listar(0, 10);
        }

        @Test
        @DisplayName("Deve listar indivíduos por levantamento")
        void deveListarIndividuosPorLevantamento() {
            PageResponse<IndividuoResponse> expectedPage = mock(PageResponse.class);

            when(individuoService.listarPorLevantamento(12L, 1, 20)).thenReturn(expectedPage);

            PageResponse<IndividuoResponse> actual = individuoController.listarPorLevantamento(12L, 1, 20);

            assertSame(expectedPage, actual);
            verify(individuoService, times(1)).listarPorLevantamento(12L, 1, 20);
        }
    }

    @Nested
    @DisplayName("Fluxos de erro")
    class ErrorFlows {

        @Test
        @DisplayName("Deve propagar RuntimeException quando serviço falhar ao criar")
        void devePropagarExcecaoAoCriar() {
            IndividuoCreateRequest request = new IndividuoCreateRequest();
            when(individuoService.criar(1L, request))
                    .thenThrow(new RuntimeException("Serviço indisponível"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> individuoController.criar(1L, request));

            assertEquals("Serviço indisponível", exception.getMessage());
            verify(individuoService, times(1)).criar(1L, request);
        }

        @Test
        @DisplayName("Deve propagar RuntimeException quando serviço falhar ao obter por id")
        void devePropagarExcecaoAoObterPorId() {
            when(individuoService.obterPorId(8L))
                    .thenThrow(new RuntimeException("Indivíduo não encontrado"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> individuoController.obterPorId(8L));

            assertEquals("Indivíduo não encontrado", exception.getMessage());
            verify(individuoService, times(1)).obterPorId(8L);
        }

        @Test
        @DisplayName("Deve propagar IllegalArgumentException quando atualizar com request nulo")
        void devePropagarIllegalArgumentExceptionAoAtualizarComRequestNulo() {
            when(individuoService.atualizar(2L, null))
                    .thenThrow(new IllegalArgumentException("Request nulo"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> individuoController.atualizar(2L, null));

            assertEquals("Request nulo", exception.getMessage());
            verify(individuoService, times(1)).atualizar(2L, null);
        }

        @Test
        @DisplayName("Deve propagar RuntimeException quando serviço falhar ao deletar")
        void devePropagarExcecaoAoDeletar() {
            doThrow(new RuntimeException("Falha ao deletar"))
                    .when(individuoService).deletar(4L);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> individuoController.deletar(4L));

            assertEquals("Falha ao deletar", exception.getMessage());
            verify(individuoService, times(1)).deletar(4L);
        }

        @Test
        @DisplayName("Deve retornar null quando serviço de listagem retornar dados nulos")
        void deveRetornarNullQuandoServicoListarRetornarNulo() {
            when(individuoService.listar(0, 5)).thenReturn(null);

            PageResponse<IndividuoResponse> actual = individuoController.listar(0, 5);

            assertNull(actual);
            verify(individuoService, times(1)).listar(0, 5);
        }
    }
}