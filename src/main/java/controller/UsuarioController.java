package controller;

import domain.dto.*;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import services.UsuarioService;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    @Inject
    UsuarioService usuarioService;

    @Inject
    JsonWebToken jwt;

    @POST
    @PermitAll
    @Operation(summary = "Criar conta de usuário")
    @APIResponse(responseCode = "200", description = "Conta criada com sucesso")
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public UsuarioResponse criarConta(UsuarioCreateRequest request) {
        return usuarioService.criarConta(request);
    }

    @GET
    @Path("/me")
    @Operation(summary = "Obter usuário logado")
    @APIResponse(responseCode = "200", description = "Usuário encontrado")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public UsuarioResponse obterPorJWT() {
        Long id = Long.parseLong(jwt.getSubject());
        return usuarioService.obterPorId(id);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obter usuário por ID")
    @APIResponse(responseCode = "200", description = "Usuário encontrado")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public UsuarioResponse obterPorId(@PathParam("id") Long id) {
        return usuarioService.obterPorId(id);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar usuário")
    @APIResponse(responseCode = "200", description = "Usuário atualizado")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public UsuarioResponse atualizar(@PathParam("id") Long id, UsuarioCreateRequest request) {
        return usuarioService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar usuário")
    @APIResponse(responseCode = "204", description = "Usuário deletado")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public void deletar(@PathParam("id") Long id) {
        usuarioService.deletar(id);
    }

    @GET
    @Operation(summary = "Listar usuários com paginação")
    @APIResponse(responseCode = "200", description = "Lista de usuários")
    public PageResponse<UsuarioResponse> listar(@QueryParam("page") @DefaultValue("0") int page,
                                                 @QueryParam("size") @DefaultValue("10") int size) {
        return usuarioService.listar(page, size);
    }
}
