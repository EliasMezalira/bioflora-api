package services;

import domain.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    
    public Optional<Usuario> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public boolean emailExists(String email) {
        return find("email", email).firstResult() != null;
    }
}
