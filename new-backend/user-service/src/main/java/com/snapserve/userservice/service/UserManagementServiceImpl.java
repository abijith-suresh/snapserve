package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.UserDetailResponse;
import com.snapserve.userservice.dto.UserSummaryResponse;
import com.snapserve.userservice.dto.PagedResponse;
import com.snapserve.userservice.exception.ResourceNotFoundException;
import com.snapserve.userservice.mapper.UserDetailMapper;
import com.snapserve.userservice.mapper.UserSummaryMapper;
import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.model.Specialist;
import com.snapserve.userservice.repository.CustomerRepository;
import com.snapserve.userservice.repository.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final CustomerRepository customerRepository;
    private final SpecialistRepository specialistRepository;

    @Override
    public PagedResponse<UserSummaryResponse> getAllUsers(String type, Pageable pageable, String search) {
        List<UserSummaryResponse> users;

        switch (type.toUpperCase()) {
            case "CUSTOMER":
                users = customerRepository.findAll(pageable)
                        .stream()
                        .filter(c -> matchesSearch(c.getFirstName(), c.getEmail(), search))
                        .map(UserSummaryMapper::fromCustomer)
                        .collect(Collectors.toList());
                break;
            case "SPECIALIST":
                users = specialistRepository.findAll(pageable)
                        .stream()
                        .filter(s -> matchesSearch(s.getFirstName(), s.getEmail(), search))
                        .map(UserSummaryMapper::fromSpecialist)
                        .collect(Collectors.toList());
                break;
            case "ALL":
            default:
                List<UserSummaryResponse> customers = customerRepository.findAll(pageable)
                        .stream()
                        .filter(c -> matchesSearch(c.getFirstName(), c.getEmail(), search))
                        .map(UserSummaryMapper::fromCustomer)
                        .toList();

                List<UserSummaryResponse> specialists = specialistRepository.findAll(pageable)
                        .stream()
                        .filter(s -> matchesSearch(s.getFirstName(), s.getEmail(), search))
                        .map(UserSummaryMapper::fromSpecialist)
                        .toList();

                users = Stream.concat(customers.stream(), specialists.stream())
                        .collect(Collectors.toList());
                break;
        }

        return PagedResponse.<UserSummaryResponse>builder()
                .content(users)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(users.size())
                .totalPages((int) Math.ceil((double) users.size() / pageable.getPageSize()))
                .last(users.size() <= pageable.getPageSize())
                .build();
    }

    @Override
    public UserDetailResponse getUserDetails(String id) {
        Customer customer = customerRepository.findById(new ObjectId(id)).orElse(null);
        if (customer != null) {
            return UserDetailMapper.fromCustomer(customer);
        }

        Specialist specialist = specialistRepository.findById(new ObjectId(id)).orElse(null);
        if (specialist != null) {
            return UserDetailMapper.fromSpecialist(specialist);
        }

        throw new ResourceNotFoundException("User", id);
    }

    private boolean matchesSearch(String name, String email, String search) {
        if (search == null || search.isEmpty()) return true;
        String lowerSearch = search.toLowerCase();
        return (name != null && name.toLowerCase().contains(lowerSearch)) ||
                (email != null && email.toLowerCase().contains(lowerSearch));
    }
}
