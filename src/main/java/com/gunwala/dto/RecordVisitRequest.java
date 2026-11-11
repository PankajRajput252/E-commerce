package com.gunwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** visitorId: random id kept in the browser. userId: nodeId when logged in. */
public record RecordVisitRequest(@NotBlank @Size(max = 64) String visitorId, @Size(max = 100) String userId) {}
