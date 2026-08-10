package services;

import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import domain.entity.Imagem;
import domain.entity.Individuo;
import domain.entity.Levantamento;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividuoIAServiceTest {

    @InjectMocks
    IndividuoIAService individuoIAService;

    @Mock
    IndividuoRepository individuoRepository;

    @Mock
    ImagemRepository imagemRepository;

    @Mock
    AIService aiService;

    private Individuo individuoMock;
    private Imagem imagemMock;
    private EspecieCompletaResponse respostaIAMock;

    @BeforeEach
    void setUp() {
        Levantamento levantamento = new Levantamento();
        levantamento.id = 1L;
        levantamento.cidade = "São Bento do Sul";
        levantamento.estado = "SC";
        levantamento.pais = "Brasil";
        levantamento.bioma = "Mata Atlântica";

        individuoMock = new Individuo();
        individuoMock.id = 10L;
        individuoMock.nomePopular = "Araucária";
        individuoMock.nomeCientifico = "Araucaria sp.";
        individuoMock.levantamento = levantamento;

        imagemMock = new Imagem();
        imagemMock.id = 100L;
        imagemMock.nome = "folha.jpg";
        imagemMock.tipoMime = "image/jpeg";
        imagemMock.conteudo = new byte[]{1, 2, 3, 4};

        EspecieCompletaResponse.TaxonDTO taxon = new EspecieCompletaResponse.TaxonDTO();
        taxon.setNomeComumConfirmado("Pinheiro-do-Paraná");
        taxon.setEspecie("Araucaria angustifolia");

        respostaIAMock = new EspecieCompletaResponse();
        respostaIAMock.setTaxon(taxon);
    }

    @Nested
    @DisplayName("completarDadosComIA")
    class CompletarDadosComIA {

        @Test
        @DisplayName("Deve consultar IA e atualizar o indivíduo com sucesso")
        void deveCompletarDadosComSucesso() {
            when(individuoRepository.findById(10L)).thenReturn(individuoMock);
            when(imagemRepository.findByIndividuo(individuoMock)).thenReturn(List.of(imagemMock));
            when(aiService.consultaEspecieIA(any(ConsultaEspecieRequest.class))).thenReturn(respostaIAMock);
            doNothing().when(individuoRepository).persist(any(Individuo.class));

            EspecieCompletaResponse response = individuoIAService.completarDadosComIA(10L);

            assertNotNull(response);
            assertEquals("Pinheiro-do-Paraná", individuoMock.nomePopular);
            assertEquals("Araucaria angustifolia", individuoMock.nomeCientifico);
            verify(aiService, times(1)).consultaEspecieIA(any(ConsultaEspecieRequest.class));
            verify(individuoRepository, times(1)).persist(individuoMock);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando o indivíduo não existir")
        void deveLancarExcecaoQuandoIndividuoNaoEncontrado() {
            when(individuoRepository.findById(99L)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> individuoIAService.completarDadosComIA(99L));

            verify(imagemRepository, never()).findByIndividuo(any());
            verify(aiService, never()).consultaEspecieIA(any());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException quando o indivíduo não possuir imagens")
        void deveLancarExcecaoQuandoNaoHouverImagens() {
            when(individuoRepository.findById(10L)).thenReturn(individuoMock);
            when(imagemRepository.findByIndividuo(individuoMock)).thenReturn(Collections.emptyList());

            assertThrows(BadRequestException.class, () -> individuoIAService.completarDadosComIA(10L));

            verify(aiService, never()).consultaEspecieIA(any());
            verify(individuoRepository, never()).persist(any(Individuo.class));
        }
    }

    @Nested
    @DisplayName("atualizarIndividuoComDadosIA")
    class AtualizarIndividuoComDadosIA {

        @Test
        @DisplayName("Não deve atualizar nem persistir se a resposta da IA for nula")
        void naoDeveAtualizarSeRespostaForNula() {
            individuoIAService.atualizarIndividuoComDadosIA(individuoMock, null);

            verify(individuoRepository, never()).persist(any(Individuo.class));
        }

        @Test
        @DisplayName("Não deve atualizar nem persistir se o taxon da resposta for nulo")
        void naoDeveAtualizarSeTaxonForNulo() {
            EspecieCompletaResponse respostaSemTaxon = new EspecieCompletaResponse();
            respostaSemTaxon.setTaxon(null);

            individuoIAService.atualizarIndividuoComDadosIA(individuoMock, respostaSemTaxon);

            verify(individuoRepository, never()).persist(any(Individuo.class));
        }
    }
}