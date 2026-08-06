package domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_imagem")
public class Imagem extends PanacheEntity {
    @Column(nullable = false, length = 255)
    public String nome;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "dados", nullable = false, columnDefinition = "bytea")
    public byte[] conteudo;

    @Column(name = "tipo_mime", nullable = false, length = 50)
    public String tipoMime;

    @Column(name = "data_upload", nullable = false, updatable = false)
    public LocalDateTime dataUpload;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "individuo_id", nullable = false)
    public Individuo individuo;

    @PrePersist
    protected void onCreate() {
        dataUpload = LocalDateTime.now();
    }

    public Imagem() {}

    public Imagem(String nome, byte[] conteudo, String tipoMime, Individuo individuo) {
        this.nome = nome;
        this.conteudo = conteudo;
        this.tipoMime = tipoMime;
        this.individuo = individuo;
    }
}
