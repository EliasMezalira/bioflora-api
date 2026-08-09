package domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ImagemDownloadResponse {
    public byte[] conteudo;
    public String nome;
    public String tipoMime;

    public ImagemDownloadResponse() {}

    public ImagemDownloadResponse(byte[] conteudo, String nome, String tipoMime) {
        this.conteudo = conteudo;
        this.nome = nome;
        this.tipoMime = tipoMime;
    }
}