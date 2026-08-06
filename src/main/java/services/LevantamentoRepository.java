package services;

import domain.entity.Levantamento;
import domain.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LevantamentoRepository implements PanacheRepository<Levantamento> {
    
    public PanacheQuery<Levantamento> findByUsuario(Usuario usuario, int page, int size) {
        return find("usuario", Sort.by("dataCriacao").descending(), usuario)
                .page(Page.of(page, size));
    }

    public long countByUsuario(Usuario usuario) {
        return count("usuario", usuario);
    }
}
