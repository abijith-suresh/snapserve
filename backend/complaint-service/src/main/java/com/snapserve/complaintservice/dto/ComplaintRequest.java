package com.snapserve.complaintservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintRequest {

    private String name;
    private String email;
    private String message;
    private String bookingId;
    private String attachments;
}