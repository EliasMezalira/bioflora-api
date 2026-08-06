package services;

import domain.entity.Imagem;
import domain.entity.Individuo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ImagemRepository implements PanacheRepository<Imagem> {
    
    public List<Imagem> findByIndividuo(Individuo individuo) {
        return list("individuo", individuo);
    }

    public long countByIndividuo(Individuo individuo) {
        return count("individuo", individuo);
    }
}
