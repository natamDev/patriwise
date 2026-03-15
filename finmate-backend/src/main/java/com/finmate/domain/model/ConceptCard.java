package com.finmate.domain.model;

public record ConceptCard(
        String name,
        String definition,
        String example,
        String risk,
        String simpleSummary
) {}
