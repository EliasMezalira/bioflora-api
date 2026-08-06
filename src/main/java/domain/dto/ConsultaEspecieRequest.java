package domain.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

public class ConsultaEspecieRequest {
    @RestForm
    public String nomePopular;

    @RestForm
    public String localizacaoCidade;

    @RestForm
    public String localizacaoEstado;

    @RestForm
    public String localizacaoPais;

    @RestForm
    public String bioma;

    @RestForm("imagensAnexo")
    @Schema(type = SchemaType.ARRAY, implementation = String.class, format = "binary")
    public List<FileUpload> imagensAnexo;
}
