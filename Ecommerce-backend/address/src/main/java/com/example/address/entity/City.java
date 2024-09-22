package com.example.address.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
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

    @Column(name = "last_update")
    @UpdateTimestamp
    private Timestamp lastUpdate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST) // Persist the country when saving a city
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @JsonIgnore
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    @ToString.Exclude // Cascade all operations to addresses
    private List<Address> addresses;
}
