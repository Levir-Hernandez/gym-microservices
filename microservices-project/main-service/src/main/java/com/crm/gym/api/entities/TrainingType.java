package com.crm.gym.api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.crm.gym.api.repositories.interfaces.Identifiable;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(
        name="training_types",
        indexes = {@Index(name = "training_types_name_idx", columnList = "name", unique = true)}
)
public class TrainingType implements Identifiable<UUID>
{
    @Id @GeneratedValue
    private UUID id;

    private String name;

    public TrainingType(UUID id) {this.id = id;}
    public TrainingType(String name) {this.name = name;}
}
