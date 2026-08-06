package domain.dto;

import java.time.LocalDateTime;

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
