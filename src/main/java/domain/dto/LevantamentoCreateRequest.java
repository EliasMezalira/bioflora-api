package domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
@RegisterForReflection
public class LevantamentoCreateRequest {
    public String nome;
    public String bioma;
    public String descricao;
    public String cidade;
    public String estado;
    public String pais;

    public LevantamentoCreateRequest() {}
}
