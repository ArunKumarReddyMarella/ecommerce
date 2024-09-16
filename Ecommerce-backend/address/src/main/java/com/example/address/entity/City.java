package com.example.address.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "city")
public class City {
    @Id
    @Column(name = "city_id")
    private String cityId;

    @NotBlank(message = "City name is required")
    @Column(name = "city")
    private String city;

    private Timestamp lastUpdate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST) // Persist the country when saving a city
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @JsonIgnore
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL) // Cascade all operations to addresses
    private List<Address> addresses;
}
