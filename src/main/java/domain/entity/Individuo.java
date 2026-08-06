package domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_individuo")
public class Individuo extends PanacheEntity {
    @Column(nullable = false, length = 50)
    public String parcela;

    @Column(name = "nome_popular", length = 100)
    public String nomePopular;

    @Column(name = "nome_cientifico", length = 200)
    public String nomeCientifico;

    @Column(name = "diametro_caule")
    public Double diametroCaule;

    @Column(name = "vivo_morto", nullable = false, length = 10)
    public String vivoMorto; // "vivo" ou "morto"

    @Column(name = "data_levantamento", nullable = false)
    public LocalDateTime dataLevantamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "levantamento_id", nullable = false)
    public Levantamento levantamento;

    @OneToMany(mappedBy = "individuo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public List<Imagem> imagens = new ArrayList<>();

    public Individuo() {}

    public Individuo(String parcela, String nomePopular, String nomeCientifico,
                     Double diametroCaule, String vivoMorto, LocalDateTime dataLevantamento,
                     Levantamento levantamento) {
        this.parcela = parcela;
        this.nomePopular = nomePopular;
        this.nomeCientifico = nomeCientifico;
        this.diametroCaule = diametroCaule;
        this.vivoMorto = vivoMorto;
        this.dataLevantamento = dataLevantamento;
        this.levantamento = levantamento;
    }
}
