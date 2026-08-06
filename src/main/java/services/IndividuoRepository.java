package services;

import domain.entity.Individuo;
import domain.entity.Levantamento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IndividuoRepository implements PanacheRepository<Individuo> {
    
    public PanacheQuery<Individuo> findByLevantamento(Levantamento levantamento, int page, int size) {
        return find("levantamento", Sort.by("dataLevantamento").descending(), levantamento)
                .page(Page.of(page, size));
    }

    public long countByLevantamento(Levantamento levantamento) {
        return count("levantamento", levantamento);
    }
}
