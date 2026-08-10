package services;

import domain.dto.LoginRequest;
import domain.dto.PageResponse;
import domain.dto.TokenResponse;
import domain.dto.UsuarioCreateRequest;
import domain.dto.UsuarioResponse;
import domain.entity.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import services.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Nested
    @DisplayName("Criar Conta")
    class CriarConta {

        @Test
        @DisplayName("Deve criar conta com sucesso quando dados forem válidos")
        void deveCriarContaComSucesso() {
            UsuarioCreateRequest request = new UsuarioCreateRequest();
            request.email = "novo@email.com";
            request.nome = "Novo Usuario";
            request.senha = "senha123";

            when(usuarioRepository.emailExists(request.email)).thenReturn(false);
            doNothing().when(usuarioRepository).persist(any(Usuario.class));

            try (MockedStatic<BcryptUtil> bcryptMock = mockStatic(BcryptUtil.class)) {
                bcryptMock.when(() -> BcryptUtil.bcryptHash("senha123")).thenReturn("hashed_senha");

                UsuarioResponse response = usuarioService.criarConta(request);

                assertNotNull(response);
                assertEquals(request.nome, response.nome);
                assertEquals(request.email, response.email);
                verify(usuarioRepository, times(1)).persist(any(Usuario.class));
            }
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando email já existir")
        void deveLancarExcecaoQuandoEmailExiste() {
            UsuarioCreateRequest request = new UsuarioCreateRequest();
            request.email = "existente@email.com";
            request.nome = "Usuario";

            when(usuarioRepository.emailExists(request.email)).thenReturn(true);

            assertThrows(BadRequestException.class, () -> usuarioService.criarConta(request));
            verify(usuarioRepository, never()).persist(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando nome for nulo ou em branco")
        void deveLancarExcecaoQuandoNomeInvalido() {
            UsuarioCreateRequest request = new UsuarioCreateRequest();
            request.email = "valido@email.com";
            request.nome = "";

            when(usuarioRepository.emailExists(request.email)).thenReturn(false);

            assertThrows(BadRequestException.class, () -> usuarioService.criarConta(request));
        }
    }

    @Nested
    @DisplayName("Login")
    class Login {

        @Test
        @DisplayName("Deve realizar login com sucesso")
        void deveRealizarLoginComSucesso() {
            LoginRequest request = new LoginRequest();
            request.email = "user@email.com";
            request.senha = "123456";

            Usuario usuario = new Usuario("User", "user@email.com", "hash_senha");
            usuario.id = 1L;

            when(usuarioRepository.findByEmail(request.email)).thenReturn(Optional.of(usuario));

            try (MockedStatic<BcryptUtil> bcryptMock = mockStatic(BcryptUtil.class)) {
                bcryptMock.when(() -> BcryptUtil.matches("123456", "hash_senha")).thenReturn(true);

                TokenResponse response = usuarioService.login(request);

                assertNotNull(response);
                assertNotNull(response.token);
            }
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando usuario não for encontrado no login")
        void deveLancarExcecaoQuandoUsuarioNaoEncontradoNoLogin() {
            LoginRequest request = new LoginRequest();
            request.email = "desconhecido@email.com";

            when(usuarioRepository.findByEmail(request.email)).thenReturn(Optional.empty());

            assertThrows(BadRequestException.class, () -> usuarioService.login(request));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando senha for incorreta")
        void deveLancarExcecaoQuandoSenhaIncorreta() {
            LoginRequest request = new LoginRequest();
            request.email = "user@email.com";
            request.senha = "senha_errada";

            Usuario usuario = new Usuario("User", "user@email.com", "hash_senha");

            when(usuarioRepository.findByEmail(request.email)).thenReturn(Optional.of(usuario));

            try (MockedStatic<BcryptUtil> bcryptMock = mockStatic(BcryptUtil.class)) {
                bcryptMock.when(() -> BcryptUtil.matches("senha_errada", "hash_senha")).thenReturn(false);

                assertThrows(BadRequestException.class, () -> usuarioService.login(request));
            }
        }
    }

    @Nested
    @DisplayName("Obter por ID")
    class ObterPorId {

        @Test
        @DisplayName("Deve retornar usuario quando ID existir")
        void deveRetornarUsuarioQuandoIdExiste() {
            Usuario usuario = new Usuario("Teste", "teste@email.com", "senha");
            usuario.id = 1L;

            when(usuarioRepository.findById(1L)).thenReturn(usuario);

            UsuarioResponse response = usuarioService.obterPorId(1L);

            assertNotNull(response);
            assertEquals(1L, response.id);
            assertEquals("Teste", response.nome);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando ID não existir")
        void deveLancarNotFoundQuandoIdNaoExiste() {
            when(usuarioRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> usuarioService.obterPorId(99L));
        }
    }

    @Nested
    @DisplayName("Atualizar e Deletar")
    class AtualizarEDeletar {

        @Test
        @DisplayName("Deve atualizar nome do usuário com sucesso")
        void deveAtualizarUsuario() {
            Usuario usuario = new Usuario("Nome Antigo", "email@teste.com", "senha");
            usuario.id = 1L;

            UsuarioCreateRequest request = new UsuarioCreateRequest();
            request.nome = "Nome Novo";

            when(usuarioRepository.findById(1L)).thenReturn(usuario);

            UsuarioResponse response = usuarioService.atualizar(1L, request);

            assertNotNull(response);
            assertEquals("Nome Novo", usuario.nome);
            verify(usuarioRepository, times(1)).persist(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve deletar usuário com sucesso")
        void deveDeletarUsuario() {
            Usuario usuario = new Usuario("User", "email@teste.com", "senha");
            usuario.id = 1L;

            when(usuarioRepository.findById(1L)).thenReturn(usuario);

            usuarioService.deletar(1L);

            verify(usuarioRepository, times(1)).delete(usuario);
        }
    }

    @Nested
    @DisplayName("Listar Paginado")
    class Listar {

        @Test
        @DisplayName("Deve retornar lista paginada de usuários")
        void deveListarUsuariosPaginado() {
            Usuario usuario = new Usuario("User", "email@teste.com", "senha");
            usuario.id = 1L;

            PanacheQuery<Usuario> queryMock = mock(PanacheQuery.class);
            when(usuarioRepository.findAll()).thenReturn(queryMock);
            when(queryMock.page(any())).thenReturn(queryMock);
            when(queryMock.list()).thenReturn(List.of(usuario));
            when(usuarioRepository.count()).thenReturn(1L);

            PageResponse<UsuarioResponse> response = usuarioService.listar(0, 10);

            assertNotNull(response);
            assertEquals(1, response.content.size());
            assertEquals(1, response.totalElements);
        }
    }
}