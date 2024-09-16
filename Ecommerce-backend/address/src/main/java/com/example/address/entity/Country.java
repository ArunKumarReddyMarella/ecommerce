package com.example.address.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
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
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Cascade all operations to cities
    private List<City> cities;
}

