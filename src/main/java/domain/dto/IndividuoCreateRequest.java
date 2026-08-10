package domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
@RegisterForReflection
public class IndividuoCreateRequest {
    public String parcela;
    public String nomePopular;
    public String nomeCientifico;
    public Double diametroCaule;
    public String vivoMorto;
    public LocalDateTime dataLevantamento;

    public IndividuoCreateRequest() {}
}
