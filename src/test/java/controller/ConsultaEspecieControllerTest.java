package controller;

import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.ConsultaEspecieService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaEspecieControllerTest {

    @InjectMocks
    ConsultaEspecieController consultaEspecieController;

    @Mock
    ConsultaEspecieService consultaEspecieService;

    @Nested
    @DisplayName("Caminho feliz")
    class HappyPath {

        @Test
        @DisplayName("Deve retornar resposta com dados de espécie quando o serviço responde com sucesso")
        void deveRetornarRespostaQuandoServicoResponderComSucesso() {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.setNomePopular("manacá");
            request.setImagemBase64("data:image/jpeg;base64,AAA");

            EspecieCompletaResponse expectedResponse = new EspecieCompletaResponse();
            expectedResponse.setNomeCientifico("Tibouchina granulosa");
            expectedResponse.setNomePopular("manacá");
            expectedResponse.setDescricao("Planta arbustiva da Mata Atlântica");

            when(consultaEspecieService.consultarComIa(request)).thenReturn(expectedResponse);

            EspecieCompletaResponse actual = consultaEspecieController.consultarEspecie(request);

            assertNotNull(actual, "A resposta não deve ser nula");
            assertEquals(expectedResponse.getNomeCientifico(), actual.getNomeCientifico());
            assertEquals(expectedResponse.getNomePopular(), actual.getNomePopular());
            assertEquals(expectedResponse.getDescricao(), actual.getDescricao());
        }
    }

    @Nested
    @DisplayName("Fluxos de erro")
    class ErrorFlows {

        @Test
        @DisplayName("Deve lançar RuntimeException quando o serviço falhar")
        void deveLancarRuntimeExceptionQuandoServicoFalhar() {
            ConsultaEspecieRequest request = new ConsultaEspecieRequest();
            request.setNomePopular("manacá");
            request.setImagemBase64("data:image/jpeg;base64,AAA");

            when(consultaEspecieService.consultarComIa(request))
                    .thenThrow(new RuntimeException("Falha na IA"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> consultaEspecieController.consultarEspecie(request));

            assertEquals("Falha na IA", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando dadosConsulta for nulo")
        void deveLancarNullPointerExceptionQuandoDadosConsultaForNulo() {
            when(consultaEspecieService.consultarComIa(null))
                    .thenThrow(new IllegalArgumentException("Dados de consulta não podem ser nulos"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> consultaEspecieController.consultarEspecie(null));

            assertEquals("Dados de consulta não podem ser nulos", exception.getMessage());
        }
    }
}