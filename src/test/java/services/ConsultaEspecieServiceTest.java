package services;

import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaEspecieServiceTest {

    @InjectMocks
    ConsultaEspecieService consultaEspecieService;

    @Mock
    AIService aiService;

    private ConsultaEspecieRequest requestMock;
    private EspecieCompletaResponse responseMock;

    @BeforeEach
    void setUp() {
        requestMock = new ConsultaEspecieRequest();
        requestMock.nomePopular = "Ipê Amarelo";
        requestMock.bioma = "Mata Atlântica";

        EspecieCompletaResponse.TaxonDTO taxon = new EspecieCompletaResponse.TaxonDTO();
        taxon.setNomeComumConfirmado("Ipê-Amarelo");
        taxon.setEspecie("Handroanthus albus");

        responseMock = new EspecieCompletaResponse();
        responseMock.setTaxon(taxon);
    }

    @Nested
    @DisplayName("consultarComIa")
    class ConsultarComIa {

        @Test
        @DisplayName("Deve delegar a consulta para o AIService e retornar a resposta com sucesso")
        void deveConsultarComSucesso() {
            when(aiService.consultaEspecieIA(requestMock)).thenReturn(responseMock);

            EspecieCompletaResponse resultado = consultaEspecieService.consultarComIa(requestMock);

            assertNotNull(resultado);
            assertNotNull(resultado.getTaxon());
            assertEquals("Ipê-Amarelo", resultado.getTaxon().getNomeComumConfirmado());
            assertEquals("Handroanthus albus", resultado.getTaxon().getEspecie());
            verify(aiService, times(1)).consultaEspecieIA(requestMock);
        }

        @Test
        @DisplayName("Deve propagar RuntimeException caso o AIService falhe")
        void devePropagarExcecaoQuandoAIServiceFalhar() {
            when(aiService.consultaEspecieIA(any()))
                    .thenThrow(new RuntimeException("Erro ao conectar com serviço de IA"));

            assertThrows(RuntimeException.class, () -> consultaEspecieService.consultarComIa(requestMock));
            verify(aiService, times(1)).consultaEspecieIA(requestMock);
        }

        @Test
        @DisplayName("Deve repassar requisição nula para o AIService")
        void deveRepassarRequisicaoNula() {
            when(aiService.consultaEspecieIA(null)).thenReturn(null);

            EspecieCompletaResponse resultado = consultaEspecieService.consultarComIa(null);

            assertNull(resultado);
            verify(aiService, times(1)).consultaEspecieIA(null);
        }
    }
}