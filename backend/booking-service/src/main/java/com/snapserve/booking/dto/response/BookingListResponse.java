package com.snapserve.booking.dto.response;

import java.util.List;

public record BookingListResponse(
    List<BookingResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {}
