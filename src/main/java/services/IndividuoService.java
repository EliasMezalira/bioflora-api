package services;

import domain.dto.IndividuoCreateRequest;
import domain.dto.IndividuoResponse;
import domain.dto.PageResponse;
import domain.entity.Individuo;
import domain.entity.Levantamento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.util.stream.Collectors;

@ApplicationScoped
public class IndividuoService {
    private static final Logger LOG = Logger.getLogger(IndividuoService.class);

    @Inject
    IndividuoRepository individuoRepository;

    @Inject
    LevantamentoRepository levantamentoRepository;

    @Transactional
    public IndividuoResponse criar(Long levantamentoId, IndividuoCreateRequest request) {
        LOG.infof("Criando indivíduo para levantamento: %d", levantamentoId);

        Levantamento levantamento = levantamentoRepository.findById(levantamentoId);
        if (levantamento == null) {
            throw new NotFoundException("Levantamento não encontrado");
        }

        validarRequest(request);

        Individuo individuo = new Individuo(
                request.parcela,
                request.nomePopular,
                request.nomeCientifico,
                request.diametroCaule,
                request.vivoMorto,
                request.dataLevantamento,
                levantamento
        );

        individuoRepository.persist(individuo);
        LOG.infof("Indivíduo criado: %d", individuo.id);
        return toResponse(individuo);
    }

    @Transactional
    public IndividuoResponse obterPorId(Long id) {
        Individuo individuo = individuoRepository.findById(id);
        if (individuo == null) {
            throw new NotFoundException("Indivíduo não encontrado");
        }
        return toResponse(individuo);
    }

    @Transactional
    public IndividuoResponse atualizar(Long id, IndividuoCreateRequest request) {
        Individuo individuo = individuoRepository.findById(id);
        if (individuo == null) {
            throw new NotFoundException("Indivíduo não encontrado");
        }

        validarRequest(request);

        individuo.parcela = request.parcela;
        individuo.nomePopular = request.nomePopular;
        individuo.nomeCientifico = request.nomeCientifico;
        individuo.diametroCaule = request.diametroCaule;
        individuo.vivoMorto = request.vivoMorto;
        individuo.dataLevantamento = request.dataLevantamento;

        individuoRepository.persist(individuo);
        return toResponse(individuo);
    }

    @Transactional
    public void deletar(Long id) {
        Individuo individuo = individuoRepository.findById(id);
        if (individuo == null) {
            throw new NotFoundException("Indivíduo não encontrado");
        }
        individuoRepository.delete(individuo);
        LOG.infof("Indivíduo deletado: %d", id);
    }

    public PageResponse<IndividuoResponse> listar(int page, int size) {
        var individuos = individuoRepository.findAll()
                .page(io.quarkus.panache.common.Page.of(page, size))
                .list();

        long total = individuoRepository.count();
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(
                individuos.stream().map(this::toResponse).collect(Collectors.toList()),
                total,
                totalPages,
                page,
                size
        );
    }

    public PageResponse<IndividuoResponse> listarPorLevantamento(Long levantamentoId, int page, int size) {
        Levantamento levantamento = levantamentoRepository.findById(levantamentoId);
        if (levantamento == null) {
            throw new NotFoundException("Levantamento não encontrado");
        }

        var individuos = individuoRepository.findByLevantamento(levantamento, page, size);
        long total = individuoRepository.countByLevantamento(levantamento);
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(
                individuos.list().stream().map(this::toResponse).collect(Collectors.toList()),
                total,
                totalPages,
                page,
                size
        );
    }

    private void validarRequest(IndividuoCreateRequest request) {
        if (request.parcela == null || request.parcela.isBlank()) {
            throw new BadRequestException("Parcela é obrigatória");
        }
        if (request.vivoMorto == null || request.vivoMorto.isBlank()) {
            throw new BadRequestException("Vivo/Morto é obrigatório");
        }
    }

    private IndividuoResponse toResponse(Individuo individuo) {
        return new IndividuoResponse(
                individuo.id,
                individuo.parcela,
                individuo.nomePopular,
                individuo.nomeCientifico,
                individuo.diametroCaule,
                individuo.vivoMorto,
                individuo.dataLevantamento,
                individuo.levantamento.id
        );
    }
}
