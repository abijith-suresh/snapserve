package com.snapserve.complaintservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "complaints")
public class Complaint {

    @Id
    private ObjectId id;

    private String name;
    private String email;
    private String message;
    private String bookingId;
    private String attachments;
    private Status status;
}