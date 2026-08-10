package services;

import domain.dto.LevantamentoCreateRequest;
import domain.dto.LevantamentoResponse;
import domain.dto.PageResponse;
import domain.entity.Levantamento;
import domain.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LevantamentoServiceTest {

    @InjectMocks
    LevantamentoService levantamentoService;

    @Mock
    LevantamentoRepository levantamentoRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    private Usuario usuarioMock;
    private LevantamentoCreateRequest requestMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario("Usuario Teste", "teste@email.com", "senha123");
        usuarioMock.id = 1L;

        requestMock = new LevantamentoCreateRequest();
        requestMock.nome = "Flora da Mata Atlântica";
        requestMock.bioma = "Mata Atlântica";
        requestMock.descricao = "Levantamento de espécies de grande porte";
        requestMock.cidade = "São Bento do Sul";
        requestMock.estado = "SC";
        requestMock.pais = "Brasil";
    }

    @Nested
    @DisplayName("Criar Levantamento")
    class Criar {

        @Test
        @DisplayName("Deve criar levantamento com sucesso")
        void deveCriarComSucesso() {
            when(usuarioRepository.findById(1L)).thenReturn(usuarioMock);
            doNothing().when(levantamentoRepository).persist(any(Levantamento.class));

            LevantamentoResponse response = levantamentoService.criar(1L, requestMock);

            assertNotNull(response);
            assertEquals(requestMock.nome, response.nome);
            assertEquals(requestMock.bioma, response.bioma);
            assertEquals(1L, response.usuarioId);
            verify(levantamentoRepository, times(1)).persist(any(Levantamento.class));
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando usuário não for encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            when(usuarioRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> levantamentoService.criar(99L, requestMock));
            verify(levantamentoRepository, never()).persist(any(Levantamento.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o nome for nulo ou vazio")
        void deveLancarExcecaoQuandoNomeInvalido() {
            requestMock.nome = "";
            when(usuarioRepository.findById(1L)).thenReturn(usuarioMock);

            assertThrows(BadRequestException.class, () -> levantamentoService.criar(1L, requestMock));
            verify(levantamentoRepository, never()).persist(any(Levantamento.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o bioma for nulo ou vazio")
        void deveLancarExcecaoQuandoBiomaInvalido() {
            requestMock.bioma = null;
            when(usuarioRepository.findById(1L)).thenReturn(usuarioMock);

            assertThrows(BadRequestException.class, () -> levantamentoService.criar(1L, requestMock));
            verify(levantamentoRepository, never()).persist(any(Levantamento.class));
        }
    }

    @Nested
    @DisplayName("Obter por ID")
    class ObterPorId {

        @Test
        @DisplayName("Deve retornar o levantamento quando o ID existir")
        void deveRetornarLevantamentoQuandoIdExiste() {
            Levantamento levantamento = new Levantamento(
                    requestMock.nome, requestMock.bioma, requestMock.descricao,
                    requestMock.cidade, requestMock.estado, requestMock.pais, usuarioMock
            );
            levantamento.id = 10L;

            when(levantamentoRepository.findById(10L)).thenReturn(levantamento);

            LevantamentoResponse response = levantamentoService.obterPorId(10L);

            assertNotNull(response);
            assertEquals(10L, response.id);
            assertEquals("Flora da Mata Atlântica", response.nome);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando o ID não existir")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(levantamentoRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> levantamentoService.obterPorId(99L));
        }
    }

    @Nested
    @DisplayName("Atualizar e Deletar")
    class AtualizarEDeletar {

        @Test
        @DisplayName("Deve atualizar o levantamento com sucesso")
        void deveAtualizarComSucesso() {
            Levantamento levantamento = new Levantamento(
                    "Nome Antigo", "Cerrado", "Desc", "Cidade", "ST", "BR", usuarioMock
            );
            levantamento.id = 10L;

            when(levantamentoRepository.findById(10L)).thenReturn(levantamento);

            LevantamentoResponse response = levantamentoService.atualizar(10L, requestMock);

            assertNotNull(response);
            assertEquals("Flora da Mata Atlântica", levantamento.nome);
            assertEquals("Mata Atlântica", levantamento.bioma);
            verify(levantamentoRepository, times(1)).persist(any(Levantamento.class));
        }

        @Test
        @DisplayName("Deve deletar levantamento com sucesso")
        void deveDeletarComSucesso() {
            Levantamento levantamento = new Levantamento(
                    requestMock.nome, requestMock.bioma, requestMock.descricao,
                    requestMock.cidade, requestMock.estado, requestMock.pais, usuarioMock
            );
            levantamento.id = 10L;

            when(levantamentoRepository.findById(10L)).thenReturn(levantamento);

            levantamentoService.deletar(10L);

            verify(levantamentoRepository, times(1)).delete(levantamento);
        }
    }

    @Nested
    @DisplayName("Listagens Paginadas")
    class Listagens {

        @Test
        @DisplayName("Deve listar todos os levantamentos paginados")
        void deveListarTodosPaginado() {
            Levantamento levantamento = new Levantamento(
                    requestMock.nome, requestMock.bioma, requestMock.descricao,
                    requestMock.cidade, requestMock.estado, requestMock.pais, usuarioMock
            );

            PanacheQuery<Levantamento> queryMock = mock(PanacheQuery.class);
            when(levantamentoRepository.findAll()).thenReturn(queryMock);
            when(queryMock.page(any())).thenReturn(queryMock);
            when(queryMock.list()).thenReturn(List.of(levantamento));
            when(levantamentoRepository.count()).thenReturn(1L);

            PageResponse<LevantamentoResponse> response = levantamentoService.listar(0, 10);

            assertNotNull(response);
            assertEquals(1, response.content.size());
            assertEquals(1, response.totalElements);
        }

        @Test
        @DisplayName("Deve listar levantamentos por usuário com sucesso")
        void deveListarPorUsuarioComSucesso() {
            Levantamento levantamento = new Levantamento(
                    requestMock.nome, requestMock.bioma, requestMock.descricao,
                    requestMock.cidade, requestMock.estado, requestMock.pais, usuarioMock
            );

            PanacheQuery<Levantamento> queryMock = mock(PanacheQuery.class);
            when(usuarioRepository.findById(1L)).thenReturn(usuarioMock);
            when(levantamentoRepository.findByUsuario(eq(usuarioMock), eq(0), eq(10))).thenReturn(queryMock);
            when(queryMock.list()).thenReturn(List.of(levantamento));
            when(levantamentoRepository.countByUsuario(usuarioMock)).thenReturn(1L);

            PageResponse<LevantamentoResponse> response = levantamentoService.listarPorUsuario(1L, 0, 10);

            assertNotNull(response);
            assertEquals(1, response.content.size());
            assertEquals(1, response.totalElements);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException ao listar por usuário inexistente")
        void deveLancarExcecaoAoListarPorUsuarioInexistente() {
            when(usuarioRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> levantamentoService.listarPorUsuario(99L, 0, 10));
        }
    }
}