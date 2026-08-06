package domain.dto;

import java.time.LocalDateTime;

public class IndividuoResponse {
    public Long id;
    public String parcela;
    public String nomePopular;
    public String nomeCientifico;
    public Double diametroCaule;
    public String vivoMorto;
    public LocalDateTime dataLevantamento;
    public Long levantamentoId;

    public IndividuoResponse() {}

    public IndividuoResponse(Long id, String parcela, String nomePopular,
                             String nomeCientifico, Double diametroCaule,
                             String vivoMorto, LocalDateTime dataLevantamento,
                             Long levantamentoId) {
        this.id = id;
        this.parcela = parcela;
        this.nomePopular = nomePopular;
        this.nomeCientifico = nomeCientifico;
        this.diametroCaule = diametroCaule;
        this.vivoMorto = vivoMorto;
        this.dataLevantamento = dataLevantamento;
        this.levantamentoId = levantamentoId;
    }
}
