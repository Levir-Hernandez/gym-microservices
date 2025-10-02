package com.crm.gym.api.dtos.trainee;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import com.crm.gym.api.dtos.user.UserChangePasswordRequest;

@Getter @Setter
public class TraineeChangePasswordRequest implements UserChangePasswordRequest
{
    @NotNull(message = "Trainee username is required")
    private String username;

    @NotNull(message = "Trainee oldPassword is required")
    private String oldPassword;

    @NotNull(message = "Trainee newPassword is required")
    private String newPassword;
}