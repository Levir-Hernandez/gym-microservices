package com.crm.gym.api.dtos.trainer;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import com.crm.gym.api.dtos.user.UserChangePasswordRequest;

@Getter @Setter
public class TrainerChangePasswordRequest implements UserChangePasswordRequest
{
    @NotNull(message = "Trainer username is required")
    private String username;

    @NotNull(message = "Trainer oldPassword is required")
    private String oldPassword;

    @NotNull(message = "Trainer newPassword is required")
    private String newPassword;
}