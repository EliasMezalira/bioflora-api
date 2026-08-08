package controller;

import domain.dto.ImagemDownloadResponse;
import domain.dto.ImagemResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import services.ImagemService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Path("/api/imagens")
@Produces(MediaType.APPLICATION_JSON)
public class ImagemController {

    @Inject
    ImagemService imagemService;

    @POST
    @Path("/individuo/{individuoId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Upload de imagem para indivíduo")
    @APIResponse(responseCode = "200", description = "Imagem enviada com sucesso")
    @APIResponse(responseCode = "400", description = "Arquivo inválido")
    @APIResponse(responseCode = "404", description = "Indivíduo não encontrado")
    public ImagemResponse upload(@PathParam("individuoId") Long individuoId,
                                  @MultipartForm UploadForm form) throws IOException {
        
        byte[] dados = Files.readAllBytes(form.arquivo.uploadedFile());
        String tipoMime = form.arquivo.contentType() != null ? form.arquivo.contentType() : "image/jpeg";
        
        return imagemService.upload(individuoId, dados, tipoMime, form.arquivo.fileName());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Download/visualizar imagem")
    @APIResponse(responseCode = "200", description = "Imagem retornada")
    @APIResponse(responseCode = "404", description = "Imagem não encontrada")
    public Response download(@PathParam("id") Long id) {
        ImagemDownloadResponse imagem = imagemService.download(id);

        return Response.ok(imagem.conteudo)
                .header("Content-Disposition", "inline; filename=\"" + imagem.nome + "\"")
                .type(imagem.tipoMime)
                .build();
    }

    @GET
    @Path("/individuo/{individuoId}")
    @Operation(summary = "Listar imagens do indivíduo")
    @APIResponse(responseCode = "200", description = "Lista de imagens")
    @APIResponse(responseCode = "404", description = "Indivíduo não encontrado")
    public List<ImagemResponse> listarPorIndividuo(@PathParam("individuoId") Long individuoId) {
        return imagemService.listarPorIndividuo(individuoId);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar imagem")
    @APIResponse(responseCode = "204", description = "Imagem deletada")
    @APIResponse(responseCode = "404", description = "Imagem não encontrada")
    public void deletar(@PathParam("id") Long id) {
        imagemService.deletar(id);
    }

    public static class UploadForm {
        @RestForm("arquivo")
        public FileUpload arquivo;
    }
}
