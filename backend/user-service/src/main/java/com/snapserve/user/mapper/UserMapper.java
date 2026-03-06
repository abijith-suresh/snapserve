package com.snapserve.user.mapper;

import com.snapserve.user.dto.customer.CustomerRequest;
import com.snapserve.user.dto.customer.CustomerResponse;
import com.snapserve.user.dto.specialist.SpecialistRequest;
import com.snapserve.user.dto.specialist.SpecialistResponse;
import com.snapserve.user.model.UserEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  @Mapping(target = "role", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  UserEntity toCustomerEntity(CustomerRequest request);

  @Mapping(target = "role", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  UserEntity toSpecialistEntity(SpecialistRequest request);

  @Mapping(target = "id", expression = "java(entity.getId().toString())")
  CustomerResponse toCustomerResponse(UserEntity entity);

  @Mapping(target = "id", expression = "java(entity.getId().toString())")
  SpecialistResponse toSpecialistResponse(UserEntity entity);

  List<CustomerResponse> toCustomerResponseList(List<UserEntity> entities);

  List<SpecialistResponse> toSpecialistResponseList(List<UserEntity> entities);
}
