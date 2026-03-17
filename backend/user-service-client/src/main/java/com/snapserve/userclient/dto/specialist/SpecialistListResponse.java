package com.snapserve.userclient.dto.specialist;

import java.util.List;

public record SpecialistListResponse(
    List<SpecialistResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {}
