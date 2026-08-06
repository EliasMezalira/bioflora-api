package controller;

import domain.dto.LevantamentoCreateRequest;
import domain.dto.LevantamentoResponse;
import domain.dto.PageResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import services.LevantamentoService;

@Path("/api/levantamentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LevantamentoController {

    @Inject
    LevantamentoService levantamentoService;

    @POST
    @Operation(summary = "Criar levantamento")
    @APIResponse(responseCode = "200", description = "Levantamento criado")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public LevantamentoResponse criar(@QueryParam("usuarioId") Long usuarioId, 
                                       LevantamentoCreateRequest request) {
        return levantamentoService.criar(usuarioId, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obter levantamento por ID")
    @APIResponse(responseCode = "200", description = "Levantamento encontrado")
    @APIResponse(responseCode = "404", description = "Levantamento não encontrado")
    public LevantamentoResponse obterPorId(@PathParam("id") Long id) {
        return levantamentoService.obterPorId(id);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar levantamento")
    @APIResponse(responseCode = "200", description = "Levantamento atualizado")
    @APIResponse(responseCode = "404", description = "Levantamento não encontrado")
    public LevantamentoResponse atualizar(@PathParam("id") Long id, LevantamentoCreateRequest request) {
        return levantamentoService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar levantamento")
    @APIResponse(responseCode = "204", description = "Levantamento deletado")
    @APIResponse(responseCode = "404", description = "Levantamento não encontrado")
    public void deletar(@PathParam("id") Long id) {
        levantamentoService.deletar(id);
    }

    @GET
    @Operation(summary = "Listar levantamentos com paginação")
    @APIResponse(responseCode = "200", description = "Lista de levantamentos")
    public PageResponse<LevantamentoResponse> listar(@QueryParam("page") @DefaultValue("0") int page,
                                                      @QueryParam("size") @DefaultValue("10") int size) {
        return levantamentoService.listar(page, size);
    }

    @GET
    @Path("/usuario/{usuarioId}")
    @Operation(summary = "Listar levantamentos do usuário")
    @APIResponse(responseCode = "200", description = "Lista de levantamentos do usuário")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public PageResponse<LevantamentoResponse> listarPorUsuario(@PathParam("usuarioId") Long usuarioId,
                                                                @QueryParam("page") @DefaultValue("0") int page,
                                                                @QueryParam("size") @DefaultValue("10") int size) {
        return levantamentoService.listarPorUsuario(usuarioId, page, size);
    }
}
