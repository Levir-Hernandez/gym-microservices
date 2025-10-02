package com.crm.gym.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString(callSuper = true, exclude = "trainings")
@EqualsAndHashCode(callSuper = true, exclude = "trainings")

@Entity
@Table(name="trainers")
public class Trainer extends User
{
    @ManyToOne
    @JoinColumn(name = "specialization_fk")
    private TrainingType specialization;

    @JsonIgnore
    @OneToMany(mappedBy = "trainer")
    private List<Training> trainings;

    public Trainer(UUID id, String firstname, String lastname, String username, String password, Boolean isActive, TrainingType specialization)
    {
        super(id, firstname, lastname, username, password, isActive);
        this.specialization = specialization;
    }

    public Trainer(UUID id) {super(id);}
    public Trainer(String username) {super(username);}
}
