package com.ecommerce.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user") // Assuming the table name is "users"
public class User {

    @Id
    private String userId;

    private String firstName;
    private String middleName;
    private String lastName;
    private String username;
    private String email;
    private String addressId; // Assuming address_id references another table
    @Column(updatable = false) // Prevent accidental updates
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp lastUpdate;
}
