package com.crm.gym.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ToString(callSuper = true, exclude = "trainings")
@EqualsAndHashCode(callSuper = true, exclude = "trainings")

@Entity
@Table(name="trainees")
public class Trainee extends User
{
    private LocalDate birthdate;
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "trainee", cascade = CascadeType.REMOVE)
    private List<Training> trainings;

    public Trainee(UUID id, String firstname, String lastname, String username, String password, Boolean isActive, LocalDate birthdate, String address)
    {
        super(id, firstname, lastname, username, password, isActive);
        this.birthdate = birthdate;
        this.address = address;
    }

    public Trainee(UUID id) {super(id);}
    public Trainee(String username) {super(username);}
}
