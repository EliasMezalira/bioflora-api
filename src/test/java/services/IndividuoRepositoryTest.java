package services;

import domain.entity.Individuo;
import domain.entity.Levantamento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividuoRepositoryTest {

    @Spy
    @InjectMocks
    IndividuoRepository individuoRepository;

    @Mock
    PanacheQuery<Individuo> panacheQuery;

    private Levantamento levantamentoMock;

    @BeforeEach
    void setUp() {
        levantamentoMock = new Levantamento();
        levantamentoMock.id = 1L;
    }

    @Nested
    @DisplayName("findByLevantamento")
    class FindByLevantamento {

        @Test
        @DisplayName("Deve retornar PanacheQuery paginada e ordenada por dataLevantamento descending")
        void deveRetornarQueryPaginadaPorLevantamento() {
            int page = 0;
            int size = 10;

            doReturn(panacheQuery).when(individuoRepository).find(eq("levantamento"), any(Sort.class), eq(levantamentoMock));
            when(panacheQuery.page(any())).thenReturn(panacheQuery);

            PanacheQuery<Individuo> resultado = individuoRepository.findByLevantamento(levantamentoMock, page, size);

            assertNotNull(resultado);
            verify(individuoRepository, times(1)).find(eq("levantamento"), any(Sort.class), eq(levantamentoMock));
            verify(panacheQuery, times(1)).page(any());
        }
    }

    @Nested
    @DisplayName("countByLevantamento")
    class CountByLevantamento {

        @Test
        @DisplayName("Deve retornar a contagem correta de indivíduos por levantamento")
        void deveRetornarContagemDeIndividuos() {
            long totalEsperado = 12L;

            doReturn(totalEsperado).when(individuoRepository).count(eq("levantamento"), eq(levantamentoMock));

            long resultado = individuoRepository.countByLevantamento(levantamentoMock);

            assertEquals(totalEsperado, resultado);
            verify(individuoRepository, times(1)).count("levantamento", levantamentoMock);
        }
    }
}