package com.snapserve.bookingservice.mapper;

import com.snapserve.bookingservice.dto.BookingRequest;
import com.snapserve.bookingservice.dto.BookingResponse;
import com.snapserve.bookingservice.model.Booking;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequest request) {
        return Booking.builder()
                .customerId(request.getCustomerId())
                .specialistId(request.getSpecialistId())
                .bookingDate(request.getBookingDate())
                .appointmentTime(request.getAppointmentTime())
                .service(request.getService())
                .status(request.getStatus())
                .price(request.getPrice())
                .build();
    }

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId().toHexString())
                .customerId(booking.getCustomerId())
                .specialistId(booking.getSpecialistId())
                .bookingDate(booking.getBookingDate())
                .appointmentTime(booking.getAppointmentTime())
                .service(booking.getService())
                .status(booking.getStatus())
                .price(booking.getPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
