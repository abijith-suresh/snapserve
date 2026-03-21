package com.snapserve.booking.service.mapper;

import com.snapserve.booking.dto.request.BookingRequest;
import com.snapserve.booking.dto.request.UpdateBookingRequest;
import com.snapserve.booking.dto.response.BookingResponse;
import com.snapserve.booking.model.Booking;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customerId", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  Booking toEntity(BookingRequest request);

  @Mapping(target = "id", expression = "java(booking.getId().toString())")
  @Mapping(
      target = "status",
      expression = "java(booking.getStatus() != null ? booking.getStatus().name() : null)")
  BookingResponse toResponse(Booking booking);

  List<BookingResponse> toResponseList(List<Booking> bookings);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customerId", ignore = true)
  @Mapping(target = "specialistId", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  void updateEntityFromRequest(UpdateBookingRequest request, @MappingTarget Booking booking);
}
