package controller;

import domain.dto.PageResponse;
import domain.dto.UsuarioCreateRequest;
import domain.dto.UsuarioResponse;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.UsuarioService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JsonWebToken jwt;

    @Nested
    @DisplayName("Caminho feliz")
    class HappyPath {

        @Test
        @DisplayName("Deve criar conta de usuário chamando o serviço")
        void deveCriarContaDeUsuarioChamandoServico() {
            UsuarioCreateRequest request = new UsuarioCreateRequest();
            UsuarioResponse expected = new UsuarioResponse();

            when(usuarioService.criarConta(request)).thenReturn(expected);

            UsuarioResponse actual = usuarioController.criarConta(request);

            assertSame(expected, actual);
            verify(usuarioService, times(1)).criarConta(request);
        }

        @Test
        @DisplayName("Deve obter usuário logado a partir do JWT")
        void deveObterUsuarioLogadoAPartirDoJWT() {
            when(jwt.getSubject()).thenReturn("42");

            UsuarioResponse expected = new UsuarioResponse();
            when(usuarioService.obterPorId(42L)).thenReturn(expected);

            UsuarioResponse actual = usuarioController.obterPorJWT();

            assertSame(expected, actual);
            verify(jwt, times(1)).getSubject();
            verify(usuarioService, times(1)).obterPorId(42L);
        }

        @Test
        @DisplayName("Deve obter usuário por id chamando o serviço")
        void deveObterUsuarioPorIdChamandoServico() {
            UsuarioResponse expected = new UsuarioResponse();

            when(usuarioService.obterPorId(5L)).thenReturn(expected);

            UsuarioResponse actual = usuarioController.obterPorId(5L);

            assertSame(expected, actual);
            verify(usuarioService, times(1)).obterPorId(5L);
        }

        @Test
        @DisplayName("Deve atualizar usuário chamando o serviço")
        void deveAtualizarUsuarioChamandoServico() {
            UsuarioCreateRequest request = new UsuarioCreateRequest();
            UsuarioResponse expected = new UsuarioResponse();

            when(usuarioService.atualizar(7L, request)).thenReturn(expected);

            UsuarioResponse actual = usuarioController.atualizar(7L, request);

            assertSame(expected, actual);
            verify(usuarioService, times(1)).atualizar(7L, request);
        }

        @Test
        @DisplayName("Deve deletar usuário chamando o serviço")
        void deveDeletarUsuarioChamandoServico() {
            usuarioController.deletar(3L);

            verify(usuarioService, times(1)).deletar(3L);
        }

        @Test
        @DisplayName("Deve listar usuários com paginação")
        void deveListarUsuariosComPaginacao() {
            @SuppressWarnings("unchecked")
            PageResponse<UsuarioResponse> expected = mock(PageResponse.class);

            when(usuarioService.listar(0, 10)).thenReturn(expected);

            PageResponse<UsuarioResponse> actual = usuarioController.listar(0, 10);

            assertSame(expected, actual);
            verify(usuarioService, times(1)).listar(0, 10);
        }
    }

    @Nested
    @DisplayName("Fluxos de erro")
    class ErrorFlows {

        @Test
        @DisplayName("Deve propagar RuntimeException quando falhar ao criar conta")
        void devePropagarExcecaoAoCriarConta() {
            UsuarioCreateRequest request = new UsuarioCreateRequest();

            when(usuarioService.criarConta(request))
                    .thenThrow(new RuntimeException("Dados inválidos"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> usuarioController.criarConta(request));

            assertEquals("Dados inválidos", exception.getMessage());
            verify(usuarioService, times(1)).criarConta(request);
        }

        @Test
        @DisplayName("Deve lançar NumberFormatException quando subject do JWT for inválido")
        void deveLancarNumberFormatExceptionQuandoSubjectJWTForInvalido() {
            when(jwt.getSubject()).thenReturn("abc");

            assertThrows(NumberFormatException.class, () -> usuarioController.obterPorJWT());

            verify(jwt, times(1)).getSubject();
        }

        @Test
        @DisplayName("Deve propagar RuntimeException quando falhar ao obter por id")
        void devePropagarExcecaoAoObterPorId() {
            when(usuarioService.obterPorId(8L))
                    .thenThrow(new RuntimeException("Usuário não encontrado"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> usuarioController.obterPorId(8L));

            assertEquals("Usuário não encontrado", exception.getMessage());
            verify(usuarioService, times(1)).obterPorId(8L);
        }

        @Test
        @DisplayName("Deve retornar null quando atualizar com request nulo")
        void deveRetornarNullQuandoAtualizarComRequestNulo() {
            when(usuarioService.atualizar(2L, null)).thenReturn(null);

            UsuarioResponse actual = usuarioController.atualizar(2L, null);

            assertNull(actual);
            verify(usuarioService, times(1)).atualizar(2L, null);
        }

        @Test
        @DisplayName("Deve propagar RuntimeException quando falhar ao deletar")
        void devePropagarExcecaoAoDeletar() {
            doThrow(new RuntimeException("Falha ao deletar"))
                    .when(usuarioService).deletar(4L);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> usuarioController.deletar(4L));

            assertEquals("Falha ao deletar", exception.getMessage());
            verify(usuarioService, times(1)).deletar(4L);
        }

        @Test
        @DisplayName("Deve retornar null quando listagem do serviço retornar null")
        void deveRetornarNullQuandoListarRetornarNull() {
            when(usuarioService.listar(1, 5)).thenReturn(null);

            PageResponse<UsuarioResponse> actual = usuarioController.listar(1, 5);

            assertNull(actual);
            verify(usuarioService, times(1)).listar(1, 5);
        }
    }
}
