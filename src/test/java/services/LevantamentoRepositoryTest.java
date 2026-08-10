package services;

import domain.entity.Levantamento;
import domain.entity.Usuario;
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
class LevantamentoRepositoryTest {

    @Spy
    @InjectMocks
    LevantamentoRepository levantamentoRepository;

    @Mock
    PanacheQuery<Levantamento> panacheQuery;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario("Usuario Teste", "teste@email.com", "senha123");
        usuarioMock.id = 1L;
    }

    @Nested
    @DisplayName("findByUsuario")
    class FindByUsuario {

        @Test
        @DisplayName("Deve retornar PanacheQuery paginada e ordenada por dataCriacao descending")
        void deveRetornarQueryPaginadaPorUsuario() {
            int page = 0;
            int size = 10;

            doReturn(panacheQuery).when(levantamentoRepository).find(eq("usuario"), any(Sort.class), eq(usuarioMock));
            when(panacheQuery.page(any())).thenReturn(panacheQuery);

            PanacheQuery<Levantamento> resultado = levantamentoRepository.findByUsuario(usuarioMock, page, size);

            assertNotNull(resultado);
            verify(levantamentoRepository, times(1)).find(eq("usuario"), any(Sort.class), eq(usuarioMock));
            verify(panacheQuery, times(1)).page(any());
        }
    }

    @Nested
    @DisplayName("countByUsuario")
    class CountByUsuario {

        @Test
        @DisplayName("Deve retornar a contagem correta de levantamentos do usuário")
        void deveRetornarContagemDeLevantamentos() {
            long totalEsperado = 5L;

            doReturn(totalEsperado).when(levantamentoRepository).count(eq("usuario"), eq(usuarioMock));

            long resultado = levantamentoRepository.countByUsuario(usuarioMock);

            assertEquals(totalEsperado, resultado);
            verify(levantamentoRepository, times(1)).count("usuario", usuarioMock);
        }
    }
}