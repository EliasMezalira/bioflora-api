package controller;

import domain.dto.IndividuoCreateRequest;
import domain.dto.IndividuoResponse;
import domain.dto.PageResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import services.IndividuoService;

@Path("/api/individuos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndividuoController {

    @Inject
    IndividuoService individuoService;

    @POST
    @Path("/levantamento/{levantamentoId}")
    @Operation(summary = "Criar indivíduo em um levantamento")
    @APIResponse(responseCode = "200", description = "Indivíduo criado")
    @APIResponse(responseCode = "404", description = "Levantamento não encontrado")
    public IndividuoResponse criar(@PathParam("levantamentoId") Long levantamentoId,
                                    IndividuoCreateRequest request) {
        return individuoService.criar(levantamentoId, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obter indivíduo por ID")
    @APIResponse(responseCode = "200", description = "Indivíduo encontrado")
    @APIResponse(responseCode = "404", description = "Indivíduo não encontrado")
    public IndividuoResponse obterPorId(@PathParam("id") Long id) {
        return individuoService.obterPorId(id);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar indivíduo")
    @APIResponse(responseCode = "200", description = "Indivíduo atualizado")
    @APIResponse(responseCode = "404", description = "Indivíduo não encontrado")
    public IndividuoResponse atualizar(@PathParam("id") Long id, IndividuoCreateRequest request) {
        return individuoService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar indivíduo")
    @APIResponse(responseCode = "204", description = "Indivíduo deletado")
    @APIResponse(responseCode = "404", description = "Indivíduo não encontrado")
    public void deletar(@PathParam("id") Long id) {
        individuoService.deletar(id);
    }

    @GET
    @Operation(summary = "Listar indivíduos com paginação")
    @APIResponse(responseCode = "200", description = "Lista de indivíduos")
    public PageResponse<IndividuoResponse> listar(@QueryParam("page") @DefaultValue("0") int page,
                                                   @QueryParam("size") @DefaultValue("10") int size) {
        return individuoService.listar(page, size);
    }

    @GET
    @Path("/levantamento/{levantamentoId}")
    @Operation(summary = "Listar indivíduos de um levantamento")
    @APIResponse(responseCode = "200", description = "Lista de indivíduos")
    @APIResponse(responseCode = "404", description = "Levantamento não encontrado")
    public PageResponse<IndividuoResponse> listarPorLevantamento(@PathParam("levantamentoId") Long levantamentoId,
                                                                  @QueryParam("page") @DefaultValue("0") int page,
                                                                  @QueryParam("size") @DefaultValue("10") int size) {
        return individuoService.listarPorLevantamento(levantamentoId, page, size);
    }
}
