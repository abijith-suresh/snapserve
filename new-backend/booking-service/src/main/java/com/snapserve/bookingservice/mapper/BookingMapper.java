package com.snapserve.bookingservice.mapper;

import com.snapserve.bookingservice.client.UserClient;
import com.snapserve.bookingservice.dto.BookingRequest;
import com.snapserve.bookingservice.dto.BookingResponse;
import com.snapserve.bookingservice.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserClient userClient;

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
                .customer(userClient.getUserInfoById(booking.getCustomerId()))
                .specialist(userClient.getUserInfoById(booking.getSpecialistId()))
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
