package domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection

public record ChatMessage(
        String role,
        Object content // Alterado para Object: pode ser String ou List<ContentPart>
) {}