package services;

import domain.dto.LevantamentoCreateRequest;
import domain.dto.LevantamentoResponse;
import domain.dto.PageResponse;
import domain.entity.Levantamento;
import domain.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ApplicationScoped
public class LevantamentoService {
    private static final Logger LOG = Logger.getLogger(LevantamentoService.class);

    @Inject
    LevantamentoRepository levantamentoRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public LevantamentoResponse criar(Long usuarioId, LevantamentoCreateRequest request) {
        LOG.infof("Criando levantamento para usuário: %d", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado");
        }

        validarRequest(request);

        Levantamento levantamento = new Levantamento(
                request.nome,
                request.bioma,
                request.descricao,
                request.cidade,
                request.estado,
                request.pais,
                usuario
        );

        levantamentoRepository.persist(levantamento);
        LOG.infof("Levantamento criado: %d", levantamento.id);
        return toResponse(levantamento);
    }

    @Transactional
    public LevantamentoResponse obterPorId(Long id) {
        Levantamento levantamento = levantamentoRepository.findById(id);
        if (levantamento == null) {
            throw new NotFoundException("Levantamento não encontrado");
        }
        return toResponse(levantamento);
    }

    @Transactional
    public LevantamentoResponse atualizar(Long id, LevantamentoCreateRequest request) {
        Levantamento levantamento = levantamentoRepository.findById(id);
        if (levantamento == null) {
            throw new NotFoundException("Levantamento não encontrado");
        }

        validarRequest(request);

        levantamento.nome = request.nome;
        levantamento.bioma = request.bioma;
        levantamento.descricao = request.descricao;
        levantamento.cidade = request.cidade;
        levantamento.estado = request.estado;
        levantamento.pais = request.pais;

        levantamentoRepository.persist(levantamento);
        return toResponse(levantamento);
    }

    @Transactional
    public void deletar(Long id) {
        Levantamento levantamento = levantamentoRepository.findById(id);
        if (levantamento == null) {
            throw new NotFoundException("Levantamento não encontrado");
        }
        levantamentoRepository.delete(levantamento);
        LOG.infof("Levantamento deletado: %d", id);
    }

    public PageResponse<LevantamentoResponse> listar(int page, int size) {
        var levantamentos = levantamentoRepository.findAll()
                .page(io.quarkus.panache.common.Page.of(page, size))
                .list();

        long total = levantamentoRepository.count();
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(
                levantamentos.stream().map(this::toResponse).collect(Collectors.toList()),
                total,
                totalPages,
                page,
                size
        );
    }

    public PageResponse<LevantamentoResponse> listarPorUsuario(Long usuarioId, int page, int size) {
        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado");
        }

        var levantamentos = levantamentoRepository.findByUsuario(usuario, page, size);
        long total = levantamentoRepository.countByUsuario(usuario);
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(
                levantamentos.list().stream().map(this::toResponse).collect(Collectors.toList()),
                total,
                totalPages,
                page,
                size
        );
    }

    private void validarRequest(LevantamentoCreateRequest request) {
        if (request.nome == null || request.nome.isBlank()) {
            throw new BadRequestException("Nome é obrigatório");
        }
        if (request.bioma == null || request.bioma.isBlank()) {
            throw new BadRequestException("Bioma é obrigatório");
        }
    }

    private LevantamentoResponse toResponse(Levantamento levantamento) {
        return new LevantamentoResponse(
                levantamento.id,
                levantamento.nome,
                levantamento.bioma,
                levantamento.descricao,
                levantamento.cidade,
                levantamento.estado,
                levantamento.pais,
                levantamento.dataCriacao,
                levantamento.dataAtualizacao,
                levantamento.usuario.id
        );
    }
}
