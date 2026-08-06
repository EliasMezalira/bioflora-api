package controller;

import domain.dto.LoginRequest;
import domain.dto.TokenResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import services.UsuarioService;

@Path("/api/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginController {

    @Inject
    UsuarioService usuarioService;

    @POST
    @PermitAll
    @Operation(summary = "Login do usuário")
    @APIResponse(responseCode = "200", description = "Login bem-sucedido, retorna token JWT")
    @APIResponse(responseCode = "400", description = "Email ou senha inválidos")
    public TokenResponse login(LoginRequest request) {
        return usuarioService.login(request);
    }
}
