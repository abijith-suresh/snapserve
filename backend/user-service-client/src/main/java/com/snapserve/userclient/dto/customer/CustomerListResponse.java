package com.snapserve.userclient.dto.customer;

import java.util.List;

public record CustomerListResponse(
    List<CustomerResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {}
