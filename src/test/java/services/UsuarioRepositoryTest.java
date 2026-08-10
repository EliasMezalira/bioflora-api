package services;

import domain.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryTest {

    @Spy
    @InjectMocks
    UsuarioRepository usuarioRepository;

    @Mock
    PanacheQuery<Usuario> panacheQuery;

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("Deve retornar Optional com usuário quando o email for encontrado")
        void deveRetornarUsuarioQuandoEmailExiste() {
            String email = "teste@email.com";
            Usuario usuarioEsperado = new Usuario("Teste", email, "senha123");

            doReturn(panacheQuery).when(usuarioRepository).find(eq("email"), eq(email));
            when(panacheQuery.firstResultOptional()).thenReturn(Optional.of(usuarioEsperado));

            Optional<Usuario> resultado = usuarioRepository.findByEmail(email);

            assertTrue(resultado.isPresent());
            assertEquals(email, resultado.get().email);
            verify(usuarioRepository, times(1)).find("email", email);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando o email não for encontrado")
        void deveRetornarVazioQuandoEmailNaoExiste() {
            String email = "naoexistente@email.com";

            doReturn(panacheQuery).when(usuarioRepository).find(eq("email"), eq(email));
            when(panacheQuery.firstResultOptional()).thenReturn(Optional.empty());

            Optional<Usuario> resultado = usuarioRepository.findByEmail(email);

            assertTrue(resultado.isEmpty());
            verify(usuarioRepository, times(1)).find("email", email);
        }
    }

    @Nested
    @DisplayName("emailExists")
    class EmailExists {

        @Test
        @DisplayName("Deve retornar true quando o email já estiver cadastrado")
        void deveRetornarTrueQuandoEmailExiste() {
            String email = "cadastrado@email.com";
            Usuario usuario = new Usuario("User", email, "senha");

            doReturn(panacheQuery).when(usuarioRepository).find(eq("email"), eq(email));
            when(panacheQuery.firstResult()).thenReturn(usuario);

            boolean existe = usuarioRepository.emailExists(email);

            assertTrue(existe);
            verify(usuarioRepository, times(1)).find("email", email);
        }

        @Test
        @DisplayName("Deve retornar false quando o email não existir")
        void deveRetornarFalseQuandoEmailNaoExiste() {
            String email = "livre@email.com";

            doReturn(panacheQuery).when(usuarioRepository).find(eq("email"), eq(email));
            when(panacheQuery.firstResult()).thenReturn(null);

            boolean existe = usuarioRepository.emailExists(email);

            assertFalse(existe);
            verify(usuarioRepository, times(1)).find("email", email);
        }
    }
}