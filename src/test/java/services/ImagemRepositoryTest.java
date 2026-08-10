package services;

import domain.entity.Imagem;
import domain.entity.Individuo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagemRepositoryTest {

    @Spy
    @InjectMocks
    ImagemRepository imagemRepository;

    private Individuo individuoMock;

    @BeforeEach
    void setUp() {
        individuoMock = new Individuo();
        individuoMock.id = 1L;
    }

    @Nested
    @DisplayName("findByIndividuo")
    class FindByIndividuo {

        @Test
        @DisplayName("Deve retornar a lista de imagens cadastradas para o indivíduo")
        void deveRetornarImagensDoIndividuo() {
            Imagem imagem = new Imagem();
            imagem.id = 10L;
            imagem.nome = "folha.jpg";

            doReturn(List.of(imagem)).when(imagemRepository).list(eq("individuo"), eq(individuoMock));

            List<Imagem> resultado = imagemRepository.findByIndividuo(individuoMock);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("folha.jpg", resultado.get(0).nome);
            verify(imagemRepository, times(1)).list("individuo", individuoMock);
        }
    }

    @Nested
    @DisplayName("countByIndividuo")
    class CountByIndividuo {

        @Test
        @DisplayName("Deve retornar a contagem correta de imagens do indivíduo")
        void deveRetornarContagemDeImagens() {
            long totalEsperado = 3L;

            doReturn(totalEsperado).when(imagemRepository).count(eq("individuo"), eq(individuoMock));

            long resultado = imagemRepository.countByIndividuo(individuoMock);

            assertEquals(totalEsperado, resultado);
            verify(imagemRepository, times(1)).count("individuo", individuoMock);
        }
    }
}