package services;

import domain.dto.IndividuoCreateRequest;
import domain.dto.IndividuoResponse;
import domain.dto.PageResponse;
import domain.entity.Individuo;
import domain.entity.Levantamento;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividuoServiceTest {

    @InjectMocks
    IndividuoService individuoService;

    @Mock
    IndividuoRepository individuoRepository;

    @Mock
    LevantamentoRepository levantamentoRepository;

    private Levantamento levantamentoMock;
    private IndividuoCreateRequest requestMock;

    @BeforeEach
    void setUp() {
        levantamentoMock = new Levantamento();
        levantamentoMock.id = 1L;

        requestMock = new IndividuoCreateRequest();
        requestMock.parcela = "Parcela 01";
        requestMock.nomePopular = "Araucária";
        requestMock.nomeCientifico = "Araucaria angustifolia";
        requestMock.diametroCaule = 45.5;
        requestMock.vivoMorto = "VIVO";
        requestMock.dataLevantamento = LocalDateTime.now();
    }

    @Nested
    @DisplayName("Criar Indivíduo")
    class Criar {

        @Test
        @DisplayName("Deve criar indivíduo com sucesso quando dados forem válidos")
        void deveCriarComSucesso() {
            when(levantamentoRepository.findById(1L)).thenReturn(levantamentoMock);
            doNothing().when(individuoRepository).persist(any(Individuo.class));

            IndividuoResponse response = individuoService.criar(1L, requestMock);

            assertNotNull(response);
            assertEquals(requestMock.parcela, response.parcela);
            assertEquals(requestMock.nomePopular, response.nomePopular);
            assertEquals(requestMock.nomeCientifico, response.nomeCientifico);
            assertEquals(1L, response.levantamentoId);
            verify(individuoRepository, times(1)).persist(any(Individuo.class));
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando levantamento não for encontrado")
        void deveLancarExcecaoQuandoLevantamentoNaoEncontrado() {
            when(levantamentoRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> individuoService.criar(99L, requestMock));
            verify(individuoRepository, never()).persist(any(Individuo.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando a parcela for nula ou em branco")
        void deveLancarExcecaoQuandoParcelaInvalida() {
            requestMock.parcela = "";
            when(levantamentoRepository.findById(1L)).thenReturn(levantamentoMock);

            assertThrows(BadRequestException.class, () -> individuoService.criar(1L, requestMock));
            verify(individuoRepository, never()).persist(any(Individuo.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando vivoMorto for nulo ou em branco")
        void deveLancarExcecaoQuandoVivoMortoInvalido() {
            requestMock.vivoMorto = null;
            when(levantamentoRepository.findById(1L)).thenReturn(levantamentoMock);

            assertThrows(BadRequestException.class, () -> individuoService.criar(1L, requestMock));
            verify(individuoRepository, never()).persist(any(Individuo.class));
        }
    }

    @Nested
    @DisplayName("Obter por ID")
    class ObterPorId {

        @Test
        @DisplayName("Deve retornar indivíduo quando o ID existir")
        void deveRetornarIndividuoQuandoIdExiste() {
            Individuo individuo = new Individuo(
                    requestMock.parcela, requestMock.nomePopular, requestMock.nomeCientifico,
                    requestMock.diametroCaule, requestMock.vivoMorto, requestMock.dataLevantamento, levantamentoMock
            );
            individuo.id = 10L;

            when(individuoRepository.findById(10L)).thenReturn(individuo);

            IndividuoResponse response = individuoService.obterPorId(10L);

            assertNotNull(response);
            assertEquals(10L, response.id);
            assertEquals("Araucária", response.nomePopular);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando o ID não existir")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(individuoRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> individuoService.obterPorId(99L));
        }
    }

    @Nested
    @DisplayName("Atualizar e Deletar")
    class AtualizarEDeletar {

        @Test
        @DisplayName("Deve atualizar indivíduo com sucesso")
        void deveAtualizarComSucesso() {
            Individuo individuo = new Individuo(
                    "Parcela Antiga", "Ipê", "Handroanthus", 30.0, "VIVO", LocalDateTime.now(), levantamentoMock
            );
            individuo.id = 10L;

            when(individuoRepository.findById(10L)).thenReturn(individuo);

            IndividuoResponse response = individuoService.atualizar(10L, requestMock);

            assertNotNull(response);
            assertEquals("Parcela 01", individuo.parcela);
            assertEquals("Araucária", individuo.nomePopular);
            verify(individuoRepository, times(1)).persist(any(Individuo.class));
        }

        @Test
        @DisplayName("Deve deletar indivíduo com sucesso")
        void deveDeletarComSucesso() {
            Individuo individuo = new Individuo(
                    requestMock.parcela, requestMock.nomePopular, requestMock.nomeCientifico,
                    requestMock.diametroCaule, requestMock.vivoMorto, requestMock.dataLevantamento, levantamentoMock
            );
            individuo.id = 10L;

            when(individuoRepository.findById(10L)).thenReturn(individuo);

            individuoService.deletar(10L);

            verify(individuoRepository, times(1)).delete(individuo);
        }
    }

    @Nested
    @DisplayName("Listagens Paginadas")
    class Listagens {

        @Test
        @DisplayName("Deve listar todos os indivíduos paginados")
        void deveListarTodosPaginado() {
            Individuo individuo = new Individuo(
                    requestMock.parcela, requestMock.nomePopular, requestMock.nomeCientifico,
                    requestMock.diametroCaule, requestMock.vivoMorto, requestMock.dataLevantamento, levantamentoMock
            );

            PanacheQuery<Individuo> queryMock = mock(PanacheQuery.class);
            when(individuoRepository.findAll()).thenReturn(queryMock);
            when(queryMock.page(any())).thenReturn(queryMock);
            when(queryMock.list()).thenReturn(List.of(individuo));
            when(individuoRepository.count()).thenReturn(1L);

            PageResponse<IndividuoResponse> response = individuoService.listar(0, 10);

            assertNotNull(response);
            assertEquals(1, response.content.size());
            assertEquals(1, response.totalElements);
        }

        @Test
        @DisplayName("Deve listar indivíduos por levantamento com sucesso")
        void deveListarPorLevantamentoComSucesso() {
            Individuo individuo = new Individuo(
                    requestMock.parcela, requestMock.nomePopular, requestMock.nomeCientifico,
                    requestMock.diametroCaule, requestMock.vivoMorto, requestMock.dataLevantamento, levantamentoMock
            );

            PanacheQuery<Individuo> queryMock = mock(PanacheQuery.class);
            when(levantamentoRepository.findById(1L)).thenReturn(levantamentoMock);
            when(individuoRepository.findByLevantamento(eq(levantamentoMock), eq(0), eq(10))).thenReturn(queryMock);
            when(queryMock.list()).thenReturn(List.of(individuo));
            when(individuoRepository.countByLevantamento(levantamentoMock)).thenReturn(1L);

            PageResponse<IndividuoResponse> response = individuoService.listarPorLevantamento(1L, 0, 10);

            assertNotNull(response);
            assertEquals(1, response.content.size());
            assertEquals(1, response.totalElements);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException ao listar por levantamento inexistente")
        void deveLancarExcecaoAoListarPorLevantamentoInexistente() {
            when(levantamentoRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> individuoService.listarPorLevantamento(99L, 0, 10));
        }
    }
}