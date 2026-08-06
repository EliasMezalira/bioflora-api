package rest;

import domain.dto.IAChatRequest;
import domain.dto.IAChatResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("")
@RegisterRestClient(configKey = "openai-api")
public interface OpenApiClient {
    @POST
    @Path("/chat/completions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    IAChatResponse enviarPrompt(
            @HeaderParam("Authorization") String authorization,
            IAChatRequest request
    );

}
