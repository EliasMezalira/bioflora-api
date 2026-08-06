package domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record IAChatRequest(
        String model,
        String reasoning_effort,
        List<ChatMessage> messages,
        Double temperature,
        @JsonProperty("response_format") ResponseFormat responseFormat
) {
    public IAChatRequest(String model, List<ChatMessage> messages, Double temperature) {
        this(model,"minimal",  messages, temperature, null);
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ContentPart(
            String type, // "text" ou "image_url"
            String text,
            @JsonProperty("image_url") ImageUrl imageUrl
    ) {
        // Factory method para facilitar a criação de texto
        public static ContentPart text(String text) {
            return new ContentPart("text", text, null);
        }

        // Factory method para facilitar a criação de imagem
        public static ContentPart image(String base64, String mimeType) {
            String url = "data:" + mimeType + ";base64," + base64;
            return new ContentPart("image_url", null, new ImageUrl(url));
        }

        public record ImageUrl(String url) {}

    }

    public record ResponseFormat(String type) {
        public static ResponseFormat jsonObject() {
            return new ResponseFormat("json_object");
        }
    }
}




