package domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TokenResponse {
    public String token;
    public String type;

    public TokenResponse() {}

    public TokenResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }
}
