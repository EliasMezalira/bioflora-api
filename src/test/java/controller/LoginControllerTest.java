package controller;

import domain.dto.LoginRequest;
import domain.dto.TokenResponse;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Caminho feliz")
    class HappyPath {

        @Test
        @DisplayName("Deve autenticar usuário e retornar token")
        void deveAutenticarUsuarioERetornarToken() {
            LoginRequest request = new LoginRequest();
            request.setEmail("usuario@teste.com");
            request.setSenha("senha123");

            TokenResponse expected = new TokenResponse();
            expected.setToken("jwt-token");

            when(usuarioService.login(request)).thenReturn(expected);

            TokenResponse actual = loginController.login(request);

            assertSame(expected, actual);
            assertEquals("jwt-token", actual.getToken());
            verify(usuarioService, times(1)).login(request);
        }
    }

    @Nested
    @DisplayName("Fluxos de erro")
    class ErrorFlows {

        @Test
        @DisplayName("Deve propagar RuntimeException quando o serviço falhar")
        void devePropagarRuntimeExceptionQuandoServicoFalhar() {
            LoginRequest request = new LoginRequest();
            request.setEmail("usuario@teste.com");
            request.setSenha("senhaErrada");

            when(usuarioService.login(request))
                    .thenThrow(new RuntimeException("Email ou senha inválidos"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> loginController.login(request));

            assertEquals("Email ou senha inválidos", exception.getMessage());
            verify(usuarioService, times(1)).login(request);
        }

        @Test
        @DisplayName("Deve retornar null quando o serviço retornar null")
        void deveRetornarNullQuandoServicoRetornarNull() {
            when(usuarioService.login(null)).thenReturn(null);

            TokenResponse actual = loginController.login(null);

            assertNull(actual);
            verify(usuarioService, times(1)).login(null);
        }
    }
}