package domain.dto;

import java.time.LocalDateTime;

public class ImagemResponse {
    public Long id;
    public String nome;
    public String tipoMime;
    public LocalDateTime dataUpload;
    public Long individuoId;

    public ImagemResponse() {}

    public ImagemResponse(Long id, String nome, String tipoMime,
                          LocalDateTime dataUpload, Long individuoId) {
        this.id = id;
        this.nome = nome;
        this.tipoMime = tipoMime;
        this.dataUpload = dataUpload;
        this.individuoId = individuoId;
    }
}
