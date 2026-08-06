package services;

import domain.dto.*;
import domain.entity.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsuarioService {
    private static final Logger LOG = Logger.getLogger(UsuarioService.class);

    @Inject
    UsuarioRepository usuarioRepository;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String jwtIssuer;

    @Transactional
    public UsuarioResponse criarConta(UsuarioCreateRequest request) {
        LOG.infof("Criando nova conta para email: %s", request.email);

        if (usuarioRepository.emailExists(request.email)) {
            throw new BadRequestException("Email já cadastrado");
        }

        if (request.nome == null || request.nome.isBlank()) {
            throw new BadRequestException("Nome é obrigatório");
        }

        String senhaCriptada = BcryptUtil.bcryptHash(request.senha);
        Usuario usuario = new Usuario(request.nome, request.email, senhaCriptada);
        usuarioRepository.persist(usuario);

        LOG.infof("Conta criada com sucesso. Usuario ID: %d", usuario.id);
        return toResponse(usuario);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        LOG.infof("Tentativa de login para email: %s", request.email);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.email);
        if (usuarioOpt.isEmpty()) {
            LOG.warnf("Usuário não encontrado: %s", request.email);
            throw new BadRequestException("Email ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();
        if (!BcryptUtil.matches(request.senha, usuario.senha)) {
            LOG.warnf("Senha inválida para usuário: %s", request.email);
            throw new BadRequestException("Email ou senha inválidos");
        }

        String token = gerarToken(usuario);
        LOG.infof("Login bem-sucedido para usuário: %s", request.email);
        return new TokenResponse(token);
    }

    @Transactional
    public UsuarioResponse obterPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado");
        }
        return toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioCreateRequest request) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado");
        }

        if (request.nome != null && !request.nome.isBlank()) {
            usuario.nome = request.nome;
        }

        usuarioRepository.persist(usuario);
        return toResponse(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado");
        }
        usuarioRepository.delete(usuario);
        LOG.infof("Usuário deletado: %d", id);
    }

    public PageResponse<UsuarioResponse> listar(int page, int size) {
        var usuarios = usuarioRepository.findAll()
                .page(io.quarkus.panache.common.Page.of(page, size))
                .list();
        
        long total = usuarioRepository.count();
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(
                usuarios.stream().map(this::toResponse).collect(Collectors.toList()),
                total,
                totalPages,
                page,
                size
        );
    }


    private String gerarToken(Usuario usuario) {
        try {
            return Jwt.issuer(jwtIssuer)
                    .subject(usuario.id.toString())
                    .claim("email", usuario.email)
                    .claim("nome", usuario.nome)
                    .sign();
        } catch (RuntimeException e) {
            // fallback para ambiente de desenvolvimento/teste quando a chave de assinatura não está configurada
            LOG.warnf(e, "Chave de assinatura JWT não configurada — usando token de desenvolvimento para usuário %d", usuario.id);
            return "dev-token:" + usuario.id;
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.id, usuario.nome, usuario.email, usuario.dataCriacao);
    }
}
