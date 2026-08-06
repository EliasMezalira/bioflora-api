package services;

import domain.dto.ImagemResponse;
import domain.entity.Imagem;
import domain.entity.Individuo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ImagemService {
    private static final Logger LOG = Logger.getLogger(ImagemService.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    @Inject
    ImagemRepository imagemRepository;

    @Inject
    IndividuoRepository individuoRepository;

    @Transactional
    public ImagemResponse upload(Long individuoId, byte[] dados, String tipoMime, String nomeArquivo) {
        LOG.infof("Fazendo upload de imagem para indivíduo: %d", individuoId);

        Individuo individuo = individuoRepository.findById(individuoId);
        if (individuo == null) {
            throw new NotFoundException("Indivíduo não encontrado");
        }

        validarArquivo(dados, tipoMime);

        Imagem imagem = new Imagem(nomeArquivo, dados, tipoMime, individuo);
        imagemRepository.persist(imagem);

        LOG.infof("Imagem salva: %d para indivíduo: %d", imagem.id, individuoId);
        return toResponse(imagem);
    }

    @Transactional
    public Imagem obterPorId(Long id) {
        Imagem imagem = imagemRepository.findById(id);
        if (imagem == null) {
            throw new NotFoundException("Imagem não encontrada");
        }
        return imagem;
    }

    @Transactional
    public void deletar(Long id) {
        Imagem imagem = imagemRepository.findById(id);
        if (imagem == null) {
            throw new NotFoundException("Imagem não encontrada");
        }
        imagemRepository.delete(imagem);
        LOG.infof("Imagem deletada: %d", id);
    }

    public List<ImagemResponse> listarPorIndividuo(Long individuoId) {
        Individuo individuo = individuoRepository.findById(individuoId);
        if (individuo == null) {
            throw new NotFoundException("Indivíduo não encontrado");
        }

        return imagemRepository.findByIndividuo(individuo)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void validarArquivo(byte[] dados, String tipoMime) {
        if (dados == null || dados.length == 0) {
            throw new BadRequestException("Arquivo vazio");
        }

        if (dados.length > MAX_FILE_SIZE) {
            throw new BadRequestException("Arquivo excede tamanho máximo de 10MB");
        }

        boolean tipoValido = false;
        for (String tipo : ALLOWED_TYPES) {
            if (tipoMime.equalsIgnoreCase(tipo)) {
                tipoValido = true;
                break;
            }
        }

        if (!tipoValido) {
            throw new BadRequestException("Tipo de arquivo não permitido. Aceitos: jpeg, png, gif, webp");
        }
    }

    private ImagemResponse toResponse(Imagem imagem) {
        return new ImagemResponse(
                imagem.id,
                imagem.nome,
                imagem.tipoMime,
                imagem.dataUpload,
                imagem.individuo.id
        );
    }
}
