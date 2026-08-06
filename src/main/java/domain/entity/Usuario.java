package domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_usuario")
public class Usuario extends PanacheEntity {
    @Column(nullable = false, length = 100)
    public String nome;

    @Column(nullable = false, unique = true, length = 100)
    public String email;

    @Column(nullable = false)
    public String senha;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    public LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    public Usuario() {}

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
}
