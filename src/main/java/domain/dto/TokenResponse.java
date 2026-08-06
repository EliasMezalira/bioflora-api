package domain.dto;

public class TokenResponse {
    public String token;
    public String type;

    public TokenResponse() {}

    public TokenResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }
}
