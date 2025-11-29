package com.crm.gym.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TrainerRegistrationRequest
{
    private String firstname;
    private String lastname;
    private String specialization;
}
