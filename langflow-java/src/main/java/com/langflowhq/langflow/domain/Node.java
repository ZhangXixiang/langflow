package com.langflowhq.langflow.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record Node(
        @NotBlank String id,
        @NotBlank String type,
        Map<String, Object> config
) {}