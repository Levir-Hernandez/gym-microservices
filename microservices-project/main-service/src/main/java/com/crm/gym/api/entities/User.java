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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
        name="users",
        indexes = {@Index(name = "users_username_idx", columnList = "username", unique = true)}
)
public abstract class User implements Identifiable<UUID>
{
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private Boolean isActive;

    public User(UUID id) {this.id = id;}
    public User(String username) {this.username = username;}
}
