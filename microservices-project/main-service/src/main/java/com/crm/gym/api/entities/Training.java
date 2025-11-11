package com.crm.gym.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

import com.crm.gym.api.repositories.interfaces.Identifiable;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="trainings")
public class Training implements Identifiable<UUID>
{
    @Id @GeneratedValue
    private UUID id;

    private String name;
    private LocalDate date;
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "trainee_fk")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_fk")
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "training_type_fk")
    private TrainingType trainingType;

    public Training(UUID id) {this.id = id;}
    public Training(String name) {this.name = name;}
}
