package domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_levantamento")
public class Levantamento extends PanacheEntity {
    @Column(nullable = false, length = 100)
    public String nome;

    @Column(nullable = false, length = 50)
    public String bioma;

    @Column(length = 500)
    public String descricao;

    @Column(nullable = false, length = 100)
    public String cidade;

    @Column(nullable = false, length = 2)
    public String estado;

    @Column(nullable = false, length = 100)
    public String pais;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    public LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    public LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Levantamento() {}

    public Levantamento(String nome, String bioma, String descricao, String cidade,
                        String estado, String pais, Usuario usuario) {
        this.nome = nome;
        this.bioma = bioma;
        this.descricao = descricao;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.usuario = usuario;
    }
}
