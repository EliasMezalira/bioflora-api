package controller;

import domain.dto.EspecieCompletaResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import services.IndividuoIAService;

@Path("/api/individuos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndividuoIAController {

    @Inject
    IndividuoIAService individuoIAService;

    @POST
    @Path("/{id}/completar-dados-ia")
    @Operation(summary = "Completar dados de indivíduo usando IA")
    @APIResponse(responseCode = "200", description = "Dados preenchidos pela IA")
    @APIResponse(responseCode = "400", description = "Indivíduo sem imagens")
    @APIResponse(responseCode = "404", description = "Indivíduo não encontrado")
    public EspecieCompletaResponse completarComIA(@PathParam("id") Long individuoId) {
        return individuoIAService.completarDadosComIA(individuoId);
    }
}
