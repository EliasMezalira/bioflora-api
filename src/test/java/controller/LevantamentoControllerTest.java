package controller;

import domain.dto.LevantamentoCreateRequest;
import domain.dto.LevantamentoResponse;
import domain.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.LevantamentoService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LevantamentoControllerTest {

    @InjectMocks
    private LevantamentoController levantamentoController;

    @Mock
    private LevantamentoService levantamentoService;

    @Nested
    @DisplayName("Caminho feliz")
    class HappyPath {

        @Test
        @DisplayName("Deve criar levantamento delegando para o serviço")
        void deveCriarLevantamentoDelegandoParaServico() {
            Long usuarioId = 1L;
            LevantamentoCreateRequest request = new LevantamentoCreateRequest();
            LevantamentoResponse expected = new LevantamentoResponse();

            when(levantamentoService.criar(usuarioId, request)).thenReturn(expected);

            LevantamentoResponse actual = levantamentoController.criar(usuarioId, request);

            assertSame(expected, actual);
            verify(levantamentoService, times(1)).criar(usuarioId, request);
        }

        @Test
        @DisplayName("Deve obter levantamento por id delegando para o serviço")
        void deveObterLevantamentoPorIdDelegandoParaServico() {
            Long id = 2L;
            LevantamentoResponse expected = new LevantamentoResponse();

            when(levantamentoService.obterPorId(id)).thenReturn(expected);

            LevantamentoResponse actual = levantamentoController.obterPorId(id);

            assertSame(expected, actual);
            verify(levantamentoService, times(1)).obterPorId(id);
        }

        @Test
        @DisplayName("Deve atualizar levantamento delegando para o serviço")
        void deveAtualizarLevantamentoDelegandoParaServico() {
            Long id = 3L;
            LevantamentoCreateRequest request = new LevantamentoCreateRequest();
            LevantamentoResponse expected = new LevantamentoResponse();

            when(levantamentoService.atualizar(id, request)).thenReturn(expected);

            LevantamentoResponse actual = levantamentoController.atualizar(id, request);

            assertSame(expected, actual);
            verify(levantamentoService, times(1)).atualizar(id, request);
        }

        @Test
        @DisplayName("Deve deletar levantamento delegando para o serviço")
        void deveDeletarLevantamentoDelegandoParaServico() {
            Long id = 4L;

            levantamentoController.deletar(id);

            verify(levantamentoService, times(1)).deletar(id);
        }

        @Test
        @DisplayName("Deve listar levantamentos com paginação")
        void deveListarLevantamentosComPaginacao() {
            @SuppressWarnings("unchecked")
            PageResponse<LevantamentoResponse> expected = mock(PageResponse.class);

            when(levantamentoService.listar(0, 10)).thenReturn(expected);

            PageResponse<LevantamentoResponse> actual = levantamentoController.listar(0, 10);

            assertSame(expected, actual);
            verify(levantamentoService, times(1)).listar(0, 10);
        }

        @Test
        @DisplayName("Deve listar levantamentos do usuário com paginação")
        void deveListarLevantamentosDoUsuarioComPaginacao() {
            @SuppressWarnings("unchecked")
            PageResponse<LevantamentoResponse> expected = mock(PageResponse.class);

            when(levantamentoService.listarPorUsuario(5L, 1, 20)).thenReturn(expected);

            PageResponse<LevantamentoResponse> actual = levantamentoController.listarPorUsuario(5L, 1, 20);

            assertSame(expected, actual);
            verify(levantamentoService, times(1)).listarPorUsuario(5L, 1, 20);
        }
    }

    @Nested
    @DisplayName("Fluxos de erro")
    class ErrorFlows {

        @Test
        @DisplayName("Deve propagar exceção quando serviço falhar ao criar")
        void devePropagarExcecaoAoCriar() {
            Long usuarioId = 10L;
            LevantamentoCreateRequest request = new LevantamentoCreateRequest();

            when(levantamentoService.criar(usuarioId, request))
                    .thenThrow(new RuntimeException("Falha ao criar"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> levantamentoController.criar(usuarioId, request));

            assertEquals("Falha ao criar", exception.getMessage());
            verify(levantamentoService, times(1)).criar(usuarioId, request);
        }

        @Test
        @DisplayName("Deve propagar exceção quando serviço falhar ao obter por id")
        void devePropagarExcecaoAoObterPorId() {
            Long id = 20L;

            when(levantamentoService.obterPorId(id))
                    .thenThrow(new RuntimeException("Não encontrado"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> levantamentoController.obterPorId(id));

            assertEquals("Não encontrado", exception.getMessage());
            verify(levantamentoService, times(1)).obterPorId(id);
        }

        @Test
        @DisplayName("Deve propagar exceção quando serviço falhar ao atualizar")
        void devePropagarExcecaoAoAtualizar() {
            Long id = 30L;
            LevantamentoCreateRequest request = new LevantamentoCreateRequest();

            when(levantamentoService.atualizar(id, request))
                    .thenThrow(new RuntimeException("Falha ao atualizar"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> levantamentoController.atualizar(id, request));

            assertEquals("Falha ao atualizar", exception.getMessage());
            verify(levantamentoService, times(1)).atualizar(id, request);
        }

        @Test
        @DisplayName("Deve propagar exceção quando serviço falhar ao deletar")
        void devePropagarExcecaoAoDeletar() {
            Long id = 40L;

            doThrow(new RuntimeException("Falha ao deletar"))
                    .when(levantamentoService).deletar(id);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> levantamentoController.deletar(id));

            assertEquals("Falha ao deletar", exception.getMessage());
            verify(levantamentoService, times(1)).deletar(id);
        }

        @Test
        @DisplayName("Deve retornar null quando serviço de listagem retornar dados nulos")
        void deveRetornarNullQuandoListarRetornarNulo() {
            when(levantamentoService.listar(0, 5)).thenReturn(null);

            PageResponse<LevantamentoResponse> actual = levantamentoController.listar(0, 5);

            assertNull(actual);
            verify(levantamentoService, times(1)).listar(0, 5);
        }

        @Test
        @DisplayName("Deve propagar exceção quando serviço falhar ao listar por usuário")
        void devePropagarExcecaoAoListarPorUsuario() {
            Long usuarioId = 50L;

            when(levantamentoService.listarPorUsuario(usuarioId, 0, 10))
                    .thenThrow(new RuntimeException("Falha ao listar por usuário"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> levantamentoController.listarPorUsuario(usuarioId, 0, 10));

            assertEquals("Falha ao listar por usuário", exception.getMessage());
            verify(levantamentoService, times(1)).listarPorUsuario(usuarioId, 0, 10);
        }
    }
}