package domain.dto;

public record ChatMessage(
        String role,
        Object content // Alterado para Object: pode ser String ou List<ContentPart>
) {}