package com.langflowhq.langflow.domain;

import jakarta.validation.constraints.NotBlank;

public record Edge(
        @NotBlank String source,
        @NotBlank String target
) {}