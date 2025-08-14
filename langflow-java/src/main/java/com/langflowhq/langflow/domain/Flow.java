package com.langflowhq.langflow.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Flow(
        String id,
        @NotBlank String name,
        String description,
        @NotNull List<Node> nodes,
        @NotNull List<Edge> edges
) {}