package domain.dto;

import java.time.LocalDateTime;

public class IndividuoCreateRequest {
    public String parcela;
    public String nomePopular;
    public String nomeCientifico;
    public Double diametroCaule;
    public String vivoMorto;
    public LocalDateTime dataLevantamento;

    public IndividuoCreateRequest() {}
}
