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
@Table(name = "country")
public class Country {
    @Id
    @Column(name = "country_id")
    private String countryId;

    @NotBlank(message = "Country name is required")
    @Column(name = "country")
    private String country;

    @Column(name = "last_update")
    @UpdateTimestamp
    private Timestamp lastUpdate;

    @JsonIgnore
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude // Cascade all operations to cities
    private List<City> cities;
}

