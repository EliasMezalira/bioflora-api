package domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record IAChatResponse(
        String id,
        List<Choice> choices
) {
    public String getRespostaTexto() {
        if (choices != null && !choices.isEmpty()) {
            // Como o content agora é Object, fazemos o cast seguro para String na resposta
            Object content = choices.get(0).message().content();
            return content != null ? content.toString() : "";
        }
        return "";
    }
    public record Choice(
            Integer index,
            ChatMessage message,
            @JsonProperty("finish_reason") String finishReason
    ) {}

}






