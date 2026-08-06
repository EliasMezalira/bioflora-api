package controller;

import domain.dto.ConsultaEspecieRequest;
import domain.dto.EspecieCompletaResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.MultipartForm;
import services.ConsultaEspecieService;

@Path("/consulta-especie")
public class ConsultaEspecieController {

    @Inject
    public ConsultaEspecieService consultaEspecieService;

    @POST
    @Path("/identificacao")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Identifica uma espécie de planta usando IA")
    @APIResponse(responseCode = "200", description = "Espécie identificada e normalizada")
    @APIResponse(responseCode = "400", description = "Payload inválido")
    @APIResponse(responseCode = "502", description = "Falha ao consultar a IA")
    public EspecieCompletaResponse consultarEspecie(@MultipartForm ConsultaEspecieRequest dadosConsulta){
        return consultaEspecieService.consultarComIa(dadosConsulta);

    }
}
