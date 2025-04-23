package com.snapserve.bookingservice.repository;

import com.snapserve.bookingservice.dto.BookingResponse;
import com.snapserve.bookingservice.dto.BookingSearchCriteria;
import com.snapserve.bookingservice.dto.PagedResponse;
import com.snapserve.bookingservice.mapper.BookingMapper;
import com.snapserve.bookingservice.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class BookingRepositoryImpl implements CustomBookingRepository {

    private final MongoTemplate mongoTemplate;
    private final BookingMapper mapper;

    @Override
    public PagedResponse<BookingResponse> searchBookings(Pageable pageable, BookingSearchCriteria criteria) {
        Query query = new Query().with(pageable);

        if (criteria.getCustomerId() != null) {
            query.addCriteria(Criteria.where("customerId").is(criteria.getCustomerId()));
        }
        if (criteria.getSpecialistId() != null) {
            query.addCriteria(Criteria.where("specialistId").is(criteria.getSpecialistId()));
        }
        if (criteria.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(criteria.getStatus()));
        }
        if (criteria.getService() != null) {
            query.addCriteria(Criteria.where("service").regex(criteria.getService(), "i"));
        }
        if (criteria.getFromDate() != null) {
            query.addCriteria(Criteria.where("createdAt").gte(criteria.getFromDate()));
        }
        if (criteria.getToDate() != null) {
            query.addCriteria(Criteria.where("createdAt").lte(criteria.getToDate()));
        }

        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Booking.class);

        List<BookingResponse> responses = bookings.stream()
                .map(mapper::toResponse)
                .toList();

        return PagedResponse.<BookingResponse>builder()
                .content(responses)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(count)
                .totalPages((int) Math.ceil((double) count / pageable.getPageSize()))
                .last(pageable.getOffset() + pageable.getPageSize() >= count)
                .build();
    }
}

