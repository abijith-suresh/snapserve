package com.snapserve.bookingservice.service;

import com.snapserve.bookingservice.dto.BookingRequest;
import com.snapserve.bookingservice.dto.BookingResponse;
import com.snapserve.bookingservice.dto.BookingSearchCriteria;
import com.snapserve.bookingservice.dto.PagedResponse;
import com.snapserve.bookingservice.mapper.BookingMapper;
import com.snapserve.bookingservice.model.Booking;
import com.snapserve.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        Booking booking = mapper.toEntity(request);
        Booking saved = bookingRepository.save(booking);
        return mapper.toResponse(saved);
    }

    @Override
    public BookingResponse getBookingById(String id) {
        Booking booking = bookingRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));
        return mapper.toResponse(booking);
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBooking(String id, BookingRequest request) {
        Booking existing = bookingRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        existing.setCustomerId(request.getCustomerId());
        existing.setSpecialistId(request.getSpecialistId());
        existing.setBookingDate(request.getBookingDate());
        existing.setAppointmentTime(request.getAppointmentTime());
        existing.setService(request.getService());
        existing.setStatus(request.getStatus());
        existing.setPrice(request.getPrice());

        Booking updated = bookingRepository.save(existing);
        return mapper.toResponse(updated);
    }

    @Override
    public void deleteBooking(String id) {
        bookingRepository.deleteById(new ObjectId(id));
    }

    @Override
    public PagedResponse<BookingResponse> getBookingsWithPaginationAndSearch(Pageable pageable, BookingSearchCriteria searchCriteria) {
        return bookingRepository.searchBookings(pageable, searchCriteria);
    }

}
