package com.snapserve.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@TypeAlias("Specialist")
public class Specialist extends User{
    private String jobTitle;
    private String bio;
    private Integer yearsOfExperience;
    private List<String> certifications;

    private List<String> servicesOffered;
    private Double rating;
    private Double hourlyRate;

    private List<byte[]> portfolioImages;

    private Boolean isAvailable;
}
