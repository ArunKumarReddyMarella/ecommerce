package com.example.address.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "address")
public class Address {
    @Id
    @Column(name = "address_id")
    private String addressId;

    @NotBlank(message = "Primary address is required")
    @Size(max = 255, message = "Primary address cannot exceed 255 characters")
    @Column(name = "primary_address")
    private String primaryAddress;

    @Size(max = 255, message = "Secondary address cannot exceed 255 characters")
    @Column(name = "secondary_address")
    private String secondaryAddress;

    @NotBlank(message = "District is required")
    @Column(name = "district")
    private String district;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank(message = "Phone number is required")
    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    @Column(name = "phone")
    private String phone;

    @UpdateTimestamp
    @Column(name = "last_update")
    private Timestamp lastUpdate;

    @ManyToOne(cascade = CascadeType.PERSIST) // Only persist the city when saving an address
    @JoinColumn(name = "city_id", referencedColumnName = "city_id", nullable = false)
    private City city;
}

