package controller;

import domain.dto.EspecieCompletaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.IndividuoIAService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividuoIAControllerTest {

    @InjectMocks
    private IndividuoIAController individuoIAController;

    @Mock
    private IndividuoIAService individuoIAService;

    @Nested
    @DisplayName("Caminho feliz")
    class HappyPath {

        @Test
        @DisplayName("Deve completar dados do indivíduo usando IA e retornar resposta")
        void deveCompletarDadosDoIndividuoComSucesso() {
            Long individuoId = 123L;
            EspecieCompletaResponse expectedResponse = new EspecieCompletaResponse();
            expectedResponse.setNomeCientifico("Especie test");
            expectedResponse.setNomePopular("Nome popular");
            expectedResponse.setDescricao("Descrição gerada pela IA");

            when(individuoIAService.completarDadosComIA(individuoId)).thenReturn(expectedResponse);

            EspecieCompletaResponse actual = individuoIAController.completarComIA(individuoId);

            assertSame(expectedResponse, actual);
            assertEquals("Especie test", actual.getNomeCientifico());
            assertEquals("Nome popular", actual.getNomePopular());
            assertEquals("Descrição gerada pela IA", actual.getDescricao());
            verify(individuoIAService, times(1)).completarDadosComIA(individuoId);
        }
    }

    @Nested
    @DisplayName("Fluxos de erro")
    class ErrorFlows {

        @Test
        @DisplayName("Deve propagar RuntimeException quando o serviço falhar")
        void devePropagarRuntimeExceptionQuandoServicoFalhar() {
            Long individuoId = 456L;
            when(individuoIAService.completarDadosComIA(individuoId))
                    .thenThrow(new RuntimeException("Indivíduo não encontrado"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> individuoIAController.completarComIA(individuoId));

            assertEquals("Indivíduo não encontrado", exception.getMessage());
            verify(individuoIAService, times(1)).completarDadosComIA(individuoId);
        }

        @Test
        @DisplayName("Deve propagar IllegalArgumentException quando id do indivíduo for nulo")
        void devePropagarIllegalArgumentExceptionQuandoIdForNulo() {
            when(individuoIAService.completarDadosComIA(null))
                    .thenThrow(new IllegalArgumentException("Id do indivíduo é obrigatório"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> individuoIAController.completarComIA(null));

            assertEquals("Id do indivíduo é obrigatório", exception.getMessage());
            verify(individuoIAService, times(1)).completarDadosComIA(null);
        }
    }
}