package domain.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

public class ConsultaEspecieRequest {
    @RestForm
    public String nomePopular; // Alterado de private para public para permitir o acesso direto nos serviços

    @RestForm
    public String localizacaoCidade;

    @RestForm
    public String localizacaoEstado;

    @RestForm
    public String localizacaoPais;

    @RestForm
    public String bioma;

    @RestForm
    public String imagemBase64; // Alterado para public por consistência

    @RestForm("imagensAnexo")
    @Schema(type = SchemaType.ARRAY, implementation = String.class, format = "binary")
    public List<FileUpload> imagensAnexo;

    public String getNomePopular() {
        return nomePopular;
    }

    public void setNomePopular(String nomePopular) {
        this.nomePopular = nomePopular;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }
}