package domain.dto;

import java.time.LocalDateTime;

public class LevantamentoResponse {
    public Long id;
    public String nome;
    public String bioma;
    public String descricao;
    public String cidade;
    public String estado;
    public String pais;
    public LocalDateTime dataCriacao;
    public LocalDateTime dataAtualizacao;
    public Long usuarioId;

    public LevantamentoResponse() {}

    public LevantamentoResponse(Long id, String nome, String bioma, String descricao,
                                String cidade, String estado, String pais,
                                LocalDateTime dataCriacao, LocalDateTime dataAtualizacao,
                                Long usuarioId) {
        this.id = id;
        this.nome = nome;
        this.bioma = bioma;
        this.descricao = descricao;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.usuarioId = usuarioId;
    }
}
