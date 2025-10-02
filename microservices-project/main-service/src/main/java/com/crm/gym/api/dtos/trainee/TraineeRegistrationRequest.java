package com.crm.gym.api.dtos.trainee;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class TraineeRegistrationRequest
{
    @NotNull(message = "Trainee firstname is required")
    private String firstname;

    @NotNull(message = "Trainee lastname is required")
    private String lastname;

    private LocalDate birthdate;
    private String address;
}
