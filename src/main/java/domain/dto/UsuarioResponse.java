package domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
@RegisterForReflection
public class UsuarioResponse {
    public Long id;
    public String nome;
    public String email;
    public LocalDateTime dataCriacao;

    public UsuarioResponse() {}

    public UsuarioResponse(Long id, String nome, String email, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;
    }
}
