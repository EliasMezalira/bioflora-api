package services;

import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConsultaEspecieService {

    @Inject
    AIService aiService;

    public EspecieCompletaResponse consultarComIa(ConsultaEspecieRequest dadosConsulta) {
        return aiService.consultaEspecieIA(dadosConsulta);
    }
}
