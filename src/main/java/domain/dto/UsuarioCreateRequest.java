package domain.dto;

public class UsuarioCreateRequest {
    public String nome;
    public String email;
    public String senha;

    public UsuarioCreateRequest() {}

    public UsuarioCreateRequest(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
}
